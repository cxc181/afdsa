package com.yuqian.itax.xxljob.jobhandler.withdraw;

import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import com.xxl.job.core.log.XxlJobLogger;
import com.yuqian.itax.common.base.exception.BusinessException;
import com.yuqian.itax.common.util.CollectionUtil;
import com.yuqian.itax.order.entity.OrderEntity;
import com.yuqian.itax.order.enums.OrderTypeEnum;
import com.yuqian.itax.order.enums.RACWStatusEnum;
import com.yuqian.itax.order.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 代付提现结果跑批
 * @author：pengwei
 * @Date：2020年01月02日
 * @version：1.0
 */
@JobHandler(value="withdrawHandler")
@Component
public class WithdrawHandler extends IJobHandler {

	@Autowired
	private OrderService orderService;

	@Override
	public ReturnT<String> execute(String param) throws Exception {
		XxlJobLogger.log("代付提现任务启动");
		OrderEntity orderEntity = new OrderEntity();
		orderEntity.setOrderType(OrderTypeEnum.WITHDRAW.getValue());
		orderEntity.setOrderStatus(RACWStatusEnum.PAYING.getValue());
		List<OrderEntity> lists = orderService.select(orderEntity);
		if (CollectionUtil.isEmpty(lists)) {
			XxlJobLogger.log("无提现中订单，任务结束");
		}
		for (OrderEntity entity : lists) {
			try {
				orderService.withdrawQuery(entity);
			} catch (BusinessException e) {
				XxlJobLogger.log("代付提现失败，订单号：{}，错误原因：{}", entity.getOrderNo(), e.getMessage());
			} catch (Exception e) {
				XxlJobLogger.log("代付提现失败，订单号：{}，错误原因：{}", entity.getOrderNo(), e.getMessage());
				XxlJobLogger.log(e);
			}
		}
		XxlJobLogger.log("代付提现任务结束");
		return SUCCESS;
	}
}
