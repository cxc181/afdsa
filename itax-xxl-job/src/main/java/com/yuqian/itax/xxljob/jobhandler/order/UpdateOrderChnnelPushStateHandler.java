package com.yuqian.itax.xxljob.jobhandler.order;

import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import com.xxl.job.core.log.XxlJobLogger;
import com.yuqian.itax.order.service.OrderService;
import jdk.nashorn.internal.runtime.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 定时修改订单推送状态（5分钟执行一次）
 *
 * @Author wangkail
 * @Date 2021/5/20 10:28
 */
@JobHandler(value = "UpdateOrderChnnelPushStateHandler")
@Component
@Logger
public class UpdateOrderChnnelPushStateHandler extends IJobHandler {

    @Autowired
    private OrderService orderService;

    @Override
    public ReturnT<String> execute(String param) throws Exception {
        XxlJobLogger.log("===============定时修改订单推送状态启动");
        orderService.batchUpdateOrderChannelPushState();
        XxlJobLogger.log("=========定时修改订单推送状态结束");
        return SUCCESS;
    }
}
