package com.yuqian.itax.mq.receiver.order;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSONObject;
import com.yuqian.itax.capital.entity.UserCapitalAccountEntity;
import com.yuqian.itax.capital.service.UserCapitalAccountService;
import com.yuqian.itax.common.base.exception.BusinessException;
import com.yuqian.itax.order.entity.OrderEntity;
import com.yuqian.itax.order.entity.WithdrawOrderYishuiNotifyEntity;
import com.yuqian.itax.order.entity.dto.CreateBatchOrderDetailDto;
import com.yuqian.itax.order.entity.dto.CreateBatchOrderDto;
import com.yuqian.itax.order.service.OrderService;
import com.yuqian.itax.order.service.WithdrawOrderYishuiNotifyService;
import com.yuqian.itax.util.util.yishui.AESUtils;
import com.yuqian.itax.yishui.entity.YsMerConfig;
import com.yuqian.itax.yishui.service.YiShuiService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 易税订单回传(批次审核)
 * @author：pengwei
 * @Date：2023/2/11 11:46
 * @version：1.0
 */
@Slf4j
@Component
public class WithdrawOrderYiShuiAuditReceiver {

    @Autowired
    private WithdrawOrderYishuiNotifyService withdrawOrderYishuiNotifyService;

    @Autowired
    private YiShuiService yiShuiService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private UserCapitalAccountService userCapitalAccountService;

    @RabbitHandler
    @RabbitListener(queues = "withdrawOrderYiShuiAuditQueue", priority = "100")
    public void process(@Payload Long id) {
        try {
            //执行批次审核
           execute(id);
        } catch (BusinessException e) {
            log.error(e.getMessage());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

    }

    /**
     * 批次审核
     * @param id
     * @throws BusinessException
     */
    public void execute(Long id) throws BusinessException {

        //获取回单参数
        CreateBatchOrderDto dto = getYiShuiParam(id);

        //获取提现订单
        OrderEntity orderEntity = orderService.queryByOrderNo(dto.getTrade_number());

        //处理提现订单结果
        handleWithdrawOrder(orderEntity, dto);

    }

    /**
     * 处理提现订单结果
     * @param orderEntity
     * @param dto
     */
    public void handleWithdrawOrder(OrderEntity orderEntity, CreateBatchOrderDto dto) throws BusinessException {
        //获取易税批次详情
        CreateBatchOrderDetailDto detail = dto.getData().get(0);
        switch(detail.getResult_code()) {
            case "SUCCESS":
                //调用易税批次审核接口
                yiShuiService.changeOrderStatus(orderEntity.getOemCode(), detail.getEnterprise_order_id());
                break;
            case "FAIL":
                //易税业务处理失败
                UserCapitalAccountEntity userCapitalAccount = userCapitalAccountService.queryByUserIdAndType(orderEntity.getUserId(), 1, orderEntity.getOemCode(),2);
                if (null == userCapitalAccount) {
                    throw new BusinessException("提现失败，用户资金账户不存在");
                }
                orderService.handleCommissionWithdrawResult(orderEntity,userCapitalAccount,1, "易税业务处理失败");// 0-成功 1-失败
                break;
            default:
                throw new BusinessException("业务处理结果未知，不处理该订单");
        }
    }

    /**
     * 获取回单参数
     * @param id
     * @return
     */
    public CreateBatchOrderDto getYiShuiParam(Long id) throws BusinessException {
        log.info("收到易税提现订单批次审核，id：" + id);

        WithdrawOrderYishuiNotifyEntity entity = withdrawOrderYishuiNotifyService.findById(id);
        if (entity == null) {
            throw new BusinessException("没有查询到易税回调请求参数，id：" + id);
        }
        CreateBatchOrderDto dto = null;
        String paramResultPlaintext = entity.getParamResultPlaintext();
        if (StringUtils.isBlank(paramResultPlaintext)) {
            if (StringUtils.isBlank(entity.getMerchantCode())) {
                throw new BusinessException("机构编码为空，不处理该订单");
            }
            if (StringUtils.isBlank(entity.getParamResult())) {
                throw new BusinessException("机构编码为空，不处理该订单");
            }
            YsMerConfig config = yiShuiService.getYsParamConfig(null, entity.getMerchantCode());
            if (config == null || StringUtils.isBlank(config.getAseKey())) {
                throw new BusinessException("易税参数配置有误，请核查请求参数，机构编码：" + entity.getMerchantCode());
            }
            paramResultPlaintext = AESUtils.decrypt(entity.getParamResult(), config.getAseKey());
            if (StringUtils.isBlank(paramResultPlaintext)) {
                throw new BusinessException("解密易税请求参数结果为空，不处理该订单");
            }
        }
        dto = JSONObject.parseObject(paramResultPlaintext, CreateBatchOrderDto.class);
        if (StringUtils.isBlank(dto.getTrade_number())) {
            throw new BusinessException("提现订单编号为空，不处理该订单");
        }
        List<CreateBatchOrderDetailDto> detail = dto.getData();
        if (CollectionUtil.isEmpty(detail)) {
            throw new BusinessException("订单详情为空，不处理该订单");
        }
        CreateBatchOrderDetailDto detailDto = detail.get(0);
        if (StringUtils.isBlank(detailDto.getEnterprise_order_id())) {
            throw new BusinessException("易税提现订单批次编号id为空，不处理该订单");
        }
        return dto;
    }

}
