package com.yuqian.itax.xxljob.jobhandler.order;

import com.alibaba.fastjson.JSONObject;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import com.xxl.job.core.log.XxlJobLogger;
import com.yuqian.itax.common.util.CollectionUtil;
import com.yuqian.itax.corporateaccount.service.CorporateAccountContOrderService;
import com.yuqian.itax.order.entity.OrderEntity;
import com.yuqian.itax.order.enums.ContOrderStatusEnum;
import com.yuqian.itax.order.enums.OrderTypeEnum;
import com.yuqian.itax.order.service.OrderService;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;

/**
 * 跑批取消未支付对公户续费订单
 * @author：liumenghao
 * @Date：2021-09-14
 * @version：1.0
 */
@JobHandler(value = "CropAccContOrderCancelHandler")
@Component
public class CropAccContOrderCancelHandler extends IJobHandler {

    @Autowired
    private CorporateAccountContOrderService corporateAccountContOrderService;

    @Autowired
    private OrderService orderService;

    @Override
    public ReturnT<String> execute(String param) throws Exception {
        XxlJobLogger.log("=========跑批取消未支付托对公户费订单启动=========");

        Example example = new Example(OrderEntity.class);
        example.createCriteria()
                .andEqualTo("orderStatus", ContOrderStatusEnum.TO_BE_PAY.getValue())
                .andEqualTo("orderType", OrderTypeEnum.CORPORATE_ACCOUNT_RENEW.getValue())
                .andLessThan("addTime", new Date());

        if(StringUtils.isNotBlank(param)){
            JSONObject jsonObject = JSONObject.parseObject(param);
            String orderNo = jsonObject.getString("orderNo");
            example.createCriteria().andEqualTo("orderNo", orderNo);
        }

        List<OrderEntity> list = orderService.selectByExample(example);
        if (CollectionUtil.isEmpty(list)) {
            XxlJobLogger.log("=========没有未支付订单=========");
            XxlJobLogger.log("=========跑批取消未支付托管费续费订单结束=========");
            return SUCCESS;
        }
        for (OrderEntity entity : list) {
            corporateAccountContOrderService.cancelOrder(entity.getOrderNo(), "admin");
        }
        XxlJobLogger.log("=========跑批取消未支付托管费续费订单结束=========");
        return SUCCESS;
    }

}
