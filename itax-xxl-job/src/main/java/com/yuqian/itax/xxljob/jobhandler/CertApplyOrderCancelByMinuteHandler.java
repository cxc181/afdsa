package com.yuqian.itax.xxljob.jobhandler;

import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import com.xxl.job.core.log.XxlJobLogger;
import com.yuqian.itax.order.service.OrderService;
import com.yuqian.itax.user.entity.CompanyResoucesApplyRecordEntity;
import com.yuqian.itax.user.service.CompanyResoucesApplyRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.List;

/**
 * 证件领用订单10分钟未支付自动取消，每10分钟执行一次
 * @Author yejian
 * @Date 2019/12/28 15:34
 * @return
 */
@JobHandler(value="certApplyOrderCancelByMinuteHandler")
@Component
public class CertApplyOrderCancelByMinuteHandler extends IJobHandler {

	@Autowired
	private OrderService orderService;
	@Autowired
	private CompanyResoucesApplyRecordService companyResoucesApplyRecordService;

	@Override
	public ReturnT<String> execute(String param) throws Exception {
		long t1 = System.currentTimeMillis(); // 代码执行前时间
		XxlJobLogger.log("=========证件领用订单10分钟未支付自动取消任务启动");
		List<CompanyResoucesApplyRecordEntity> orderList = companyResoucesApplyRecordService.certApplyOrderListByType();
		if(orderList== null || orderList.size()<1){
			return SUCCESS;
		}
		orderList.stream().forEach(order ->{
			double result = (System.currentTimeMillis() - order.getAddTime().getTime()) * 1.0 / (1000 * 10 * 60);
			if(result > 1){
				XxlJobLogger.log("orderNo-->"+order.getOrderNo());
				orderService.xxljobCancelCertOrder(order.getOrderNo());
			}
		});
		XxlJobLogger.log("=========证件领用订单10分钟未支付自动取消任务结束");
		long t2 = System.currentTimeMillis(); // 代码执行后时间
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(t2 - t1);
		XxlJobLogger.log("耗时:" + c.get(Calendar.MINUTE) + "分" + c.get(Calendar.SECOND) + "秒" + c.get(Calendar.MILLISECOND) + "微秒");
		return SUCCESS;
	}
	
}
