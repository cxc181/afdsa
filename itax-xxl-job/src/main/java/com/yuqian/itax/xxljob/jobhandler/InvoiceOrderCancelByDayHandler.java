package com.yuqian.itax.xxljob.jobhandler;

import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import com.xxl.job.core.log.XxlJobLogger;
import com.yuqian.itax.order.entity.OrderEntity;
import com.yuqian.itax.order.service.InvoiceOrderService;
import com.yuqian.itax.order.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.List;

/**
 * 开票订单24小时未支付自动取消 每天凌晨00:01执行任务
 * @Author yejian
 * @Date 2019/12/28 15:34
 * @return
 */
@JobHandler(value="invoiceOrderCancelByDayHandler")
@Component
public class InvoiceOrderCancelByDayHandler extends IJobHandler {

	@Autowired
	private OrderService orderService;
	@Autowired
	private InvoiceOrderService invoiceOrderService;

	@Override
	public ReturnT<String> execute(String param) throws Exception {
		long t1 = System.currentTimeMillis(); // 代码执行前时间
		XxlJobLogger.log("=========开票订单24小时未支付自动取消任务启动");
		List<OrderEntity> orderList = orderService.invOrderListByType();
		if(orderList== null || orderList.size()<1){
			return SUCCESS;
		}
		orderList.stream().forEach(order ->{
			double result = (System.currentTimeMillis() - order.getAddTime().getTime()) * 1.0 / (1000 * 60 * 60);
			if(result > 24){
				XxlJobLogger.log("orderNo-->"+order.getOrderNo());
				invoiceOrderService.xxljobCancelInvOrder(order.getOrderNo());
			}
		});
		XxlJobLogger.log("=========开票订单24小时未支付自动取消任务结束");
		long t2 = System.currentTimeMillis(); // 代码执行后时间
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(t2 - t1);
		XxlJobLogger.log("耗时:" + c.get(Calendar.MINUTE) + "分" + c.get(Calendar.SECOND) + "秒" + c.get(Calendar.MILLISECOND) + "微秒");
		return SUCCESS;
	}
	
}
