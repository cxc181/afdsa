package com.yuqian.itax.xxljob.jobhandler.notice;

import com.alibaba.fastjson.JSON;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import com.xxl.job.core.log.XxlJobLogger;
import com.yuqian.itax.order.entity.vo.InvoiceNoticeVO;
import com.yuqian.itax.order.service.InvoiceOrderService;
import com.yuqian.itax.system.entity.DictionaryEntity;
import com.yuqian.itax.system.service.DictionaryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 *  @Author: Kaven
 *  @Date: 2020/5/20 14:30
 *  @Description: 订单状态已签收超过30个自然日，但流水状态还是待补传或者审核未通过的开票订单通知处理任务
 */ 
@Slf4j
@JobHandler(value="uploadFlowTimeoutNoticeHandler")
@Component
public class UploadFlowTimeoutNoticeHandler extends IJobHandler {

	@Autowired
	private InvoiceOrderService invoiceOrderService;
	@Autowired
	private DictionaryService dictionaryService;

	private static final Integer DEFAULT_OVER_DAYS = 30;// 默认超时天数

	@Override
	public ReturnT<String> execute(String param) throws Exception {
		XxlJobLogger.log("超时未补传流水/成果开票订单通知处理任务启动");
		Integer overDays = DEFAULT_OVER_DAYS;

		DictionaryEntity dict = this.dictionaryService.getByCode("upload_invoice_imgs_timeout");
		if(null == dict){
			XxlJobLogger.log("系统未配置补传流水/成果超时天数，取默认配置");
		}else{
			// 取系统配置天数
			overDays = Integer.parseInt(dict.getDictValue());
		}
		// 查询订单状态已签收超过30个自然日，但流水/成果状态还是待补传或者审核未通过的开票订单
		List<InvoiceNoticeVO> list = this.invoiceOrderService.selectUploadFlowTimeoutOrder(overDays);// 新增订单数

		if (CollectionUtils.isEmpty(list)) {
			XxlJobLogger.log("未找到满足条件的记录，任务结束");
			return SUCCESS;
		}

		// 新增订单数不为空，遍历集合，批量插入t_e_message_notice表
		final Integer timeOutDay = overDays;
		list.stream().forEach(invocieOrder -> {
			try {
				// 考虑到事务控制，把业务实现抽到service层
				this.invoiceOrderService.handleUploadFlowTimeoutNotice(invocieOrder,timeOutDay);
			} catch (Exception e) {
				XxlJobLogger.log("新增通知对象{} 失败:{} ", JSON.toJSONString(invocieOrder),e.getMessage());
				XxlJobLogger.log(e);
			}
		});
		XxlJobLogger.log("超时未补传流水/成果开票订单通知处理任务结束");
		return SUCCESS;
	}
}
