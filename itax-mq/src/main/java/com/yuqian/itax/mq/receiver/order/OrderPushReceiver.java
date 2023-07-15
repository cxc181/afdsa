package com.yuqian.itax.mq.receiver.order;

import com.yuqian.itax.order.dao.OrderMapper;
import com.yuqian.itax.order.entity.InvoiceOrderEntity;
import com.yuqian.itax.order.entity.OrderEntity;
import com.yuqian.itax.order.entity.vo.OpenOrderVO;
import com.yuqian.itax.order.enums.ChannelPushStateEnum;
import com.yuqian.itax.order.enums.InvoiceCreateWayEnum;
import com.yuqian.itax.order.service.InvoiceOrderService;
import com.yuqian.itax.order.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 订单推送国金
 * @author：
 * @Date：2021/5/12 15:12
 * @version：1.0
 */
@Component
@Slf4j
public class OrderPushReceiver {
    //创建十个线程
    private static  ExecutorService taskExecutor  = Executors.newFixedThreadPool(10);

    @Autowired
    private OrderService orderService;

    @Resource
    private OrderMapper orderMapper;
    @Resource
    private InvoiceOrderService invoiceOrderService;

    @RabbitHandler
    @RabbitListener(queues = "orderPush", priority="100")
    public void process(List<OpenOrderVO> listToBePush){
        if(taskExecutor == null){
            taskExecutor  = Executors.newFixedThreadPool(10);
        }
        log.info("修改订单状态为推送中");
        if (listToBePush != null && listToBePush.size()>0) {
                //  批量改成推送中
            orderMapper.batchUpdateOrderChannelPushStateByOrderNo(listToBePush,ChannelPushStateEnum.PAYING.getValue());
        }
        log.info("订单推送国金接口");
       if (listToBePush != null && listToBePush.size()>0){
            for(OpenOrderVO ov:listToBePush){
                if(ov ==null){
                    continue;
                }
                // 佣金开票订单不推送国金
                InvoiceOrderEntity entity = invoiceOrderService.queryByOrderNo(ov.getOrderNo());
                if (entity != null && InvoiceCreateWayEnum.COMMISSION.getValue().equals(entity.getCreateWay())){
                    orderMapper.updateOrderChannelPushState(ov.getOrderNo(),ChannelPushStateEnum.CANCELLED.getValue());
                    continue;
                }
                taskExecutor.execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            orderPush(ov);
                        }catch (Exception ex){
                            log.error("数据错误，推送失败!"+ex.getMessage());
                            if (StringUtils.isNotBlank(ov.getOrderNo())){
                                OrderEntity order = orderService.queryByOrderNo(ov.getOrderNo());
                                //  设置推送状态为推送失败
                                order.setChannelPushState(ChannelPushStateEnum.COMPLETED.getValue());
                                orderMapper.updateOrderChannelPushState(order.getOrderNo(),order.getChannelPushState());
                            }
                        }
                    }
                });
            }
        }else{
           log.info("暂无可推送的订单!");
        }
    }

    private void orderPush(OpenOrderVO ov){
        //国金订单tradeType 与云财订单状态标识不一致。状态转换
        //  云财     订单类型 1-充值 2-代理充值 3-提现 4-代理提现 5 - 工商注册 6-开票 7-用户升级 8-工商注销 9-证件申请 10-对公户申请 11-对公户提现 12-消费开票 13-补税 14-退税 15-托管费续费
        //  国金     订单类型 1-企业注册，2-企业开票，3-企业注销，4-托管费续费，5-个人开票，6-企业付款，7-月度交易结算，8-VIP费分润
        int tradeType = -1;
        switch (ov.getOrderType()){
            case 5 :
                tradeType = 1;
                break;
            case 6 :
                tradeType = 2;
                break;
            case 7 :
                tradeType = 8;
                break;
            case 8 :
                tradeType = 3;
                break;
            case 15 :
                tradeType = 4;
                break;
        }
        if(tradeType == -1){
            log.error("订单状态不正确!");
            if (StringUtils.isNotBlank(ov.getOrderNo())){
                OrderEntity order = orderService.queryByOrderNo(ov.getOrderNo());
                //  设置推送状态为推送失败
                order.setChannelPushState(ChannelPushStateEnum.COMPLETED.getValue());
                orderMapper.updateOrderChannelPushState(order.getOrderNo(),order.getChannelPushState());
            }
        }
        try {
            OrderEntity order = orderService.queryByOrderNo(ov.getOrderNo());
            if (order != null && (order.getChannelPushState() .equals( ChannelPushStateEnum.TO_BE_PAY.getValue() )|| order.getChannelPushState() .equals( ChannelPushStateEnum.PAYING.getValue()) || order.getChannelPushState() .equals( ChannelPushStateEnum.COMPLETED.getValue()))){
                orderService.goWJOrderPush(ov.getOrderNo(),tradeType,ov.getId(),ov.getOemCode(),ov.getCompleteTime());
            }
        }catch (Exception e){
            log.error("数据错误，推送失败!"+e.getMessage());
            if (StringUtils.isNotBlank(ov.getOrderNo())){
                OrderEntity order = orderService.queryByOrderNo(ov.getOrderNo());
                //  设置推送状态为推送失败
                order.setChannelPushState(ChannelPushStateEnum.COMPLETED.getValue());
                orderMapper.updateOrderChannelPushState(order.getOrderNo(),order.getChannelPushState());
            }
        }
    }
}
