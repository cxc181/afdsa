//package com.yuqian.itax.xxljob.jobhandler.order;
//
//import com.xxl.job.core.biz.model.ReturnT;
//import com.xxl.job.core.handler.IJobHandler;
//import com.xxl.job.core.handler.annotation.JobHandler;
//import com.xxl.job.core.log.XxlJobLogger;
//import com.yuqian.itax.common.util.CollectionUtil;
//import com.yuqian.itax.group.entity.InvoiceOrderGroupEntity;
//import com.yuqian.itax.group.service.GroupPaymentAnalysisRecordService;
//import com.yuqian.itax.group.service.InvoiceOrderGroupService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
//import java.util.List;
//
///**
// * 集团开票订单解析中跑批成出票中定时任务
// * @author：pengwei
// * @Date：2020年3月13日
// * @version：1.0
// */
//@JobHandler(value="invoiceOrderGroupTicketingHandler")
//@Component
//public class InvoiceOrderGroupTicketingHandler extends IJobHandler {
//
//	@Autowired
//	private InvoiceOrderGroupService invoiceOrderGroupService;
//
//	@Override
//	public ReturnT<String> execute(String param) throws Exception {
//		XxlJobLogger.log("集团开票订单修改状态任务启动");
//		//查询解析中集团开票订单
//		List<InvoiceOrderGroupEntity> lists = invoiceOrderGroupService.queryByStatus(0);
//		if (CollectionUtil.isEmpty(lists)) {
//			XxlJobLogger.log("暂无解析中的集团开票订单任务结束");
//			return SUCCESS;
//		}
//		for (InvoiceOrderGroupEntity entity : lists) {
//			invoiceOrderGroupService.ticketing(entity);
//		}
//		XxlJobLogger.log("集团开票订单修改状态任务结束");
//		return SUCCESS;
//	}
//}
