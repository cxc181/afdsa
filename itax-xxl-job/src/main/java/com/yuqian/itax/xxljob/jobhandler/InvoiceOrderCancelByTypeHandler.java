package com.yuqian.itax.xxljob.jobhandler;

import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import com.xxl.job.core.log.XxlJobLogger;
import com.yuqian.itax.corporateaccount.service.CorporateAccountApplyOrderService;
import com.yuqian.itax.order.entity.OrderEntity;
import com.yuqian.itax.order.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.List;
/**
     * 每天凌晨2点取消今日之前未付款的公户申请订单
 * @author：wangkail
 * @Date：2021-4-8
 * @version：
 */
@JobHandler(value="invoiceOrderCancelByTypeHandler")
@Component
public class InvoiceOrderCancelByTypeHandler extends IJobHandler {

    @Autowired
    private OrderService orderService;
    @Autowired
    private CorporateAccountApplyOrderService corporateAccountApplyOrderService;

    @Override
    public ReturnT<String> execute(String param) throws Exception {
        long t1 = System.currentTimeMillis(); // 代码执行前时间
        XxlJobLogger.log("=========对公订单未支付自动取消任务启动");
        List<OrderEntity> orderList = orderService.invOrderListByOrderType();
        if(orderList== null || orderList.size()<1){
            return SUCCESS;
        }
        orderList.stream().forEach(order ->{
            // order.getId() 取出的不是订单表的id，是t_e_corporate_account_apply_order表的id
            XxlJobLogger.log("corporateAccountApplyId-->"+order.getId());
            corporateAccountApplyOrderService.cannel(order.getId(),"admin");

        });
        XxlJobLogger.log("=========对公订单未支付自动取消任务结束");
        long t2 = System.currentTimeMillis(); // 代码执行后时间
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(t2 - t1);
        XxlJobLogger.log("耗时:" + c.get(Calendar.MINUTE) + "分" + c.get(Calendar.SECOND) + "秒" + c.get(Calendar.MILLISECOND) + "微秒");
        return SUCCESS;
    }
}
