package com.yuqian.itax.xxljob.jobhandler.withdraw;

import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import com.xxl.job.core.log.XxlJobLogger;
import com.yuqian.itax.agent.entity.OemParamsEntity;
import com.yuqian.itax.common.base.exception.BusinessException;
import com.yuqian.itax.common.util.CollectionUtil;
import com.yuqian.itax.corporateaccount.service.CorporateAccountCollectionRecordService;
import com.yuqian.itax.order.entity.OrderEntity;
import com.yuqian.itax.order.service.OrderService;
import com.yuqian.itax.system.entity.DictionaryEntity;
import com.yuqian.itax.system.service.DictionaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Description 对公户提现订单超时自动取消定时任务
 * @Author  Kaven
 * @Date   2020/10/16 14:01
*/
@JobHandler(value="corpAccountWithdrawOrderCancelHandler")
@Component
public class CorpAccountWithdrawOrderCancelHandler extends IJobHandler {

	@Autowired
	private OrderService orderService;
	@Autowired
	private CorporateAccountCollectionRecordService corporateAccountCollectionRecordService;
	@Autowired
	private DictionaryService dictionaryService;

	@Override
	public ReturnT<String> execute(String param) throws Exception {
		XxlJobLogger.log("对公户提现订单超时自动取消定时任务任务启动");
		// 读取配置，获取指定时间
		Integer minutes = 5;// 默认5分钟
		DictionaryEntity dict = this.dictionaryService.getByCode("corpAccountWithdrawOrderOvertime");
		if(null != dict){
			minutes = Integer.parseInt(dict.getDictValue());
		}

		List<OrderEntity> lists = this.orderService.selectToSubmitWithdrawOrder(minutes);
		if (CollectionUtil.isEmpty(lists)) {
			XxlJobLogger.log("无待提交的对公户订单，任务结束");
		}
		OemParamsEntity paramsEntity = null;
		for (OrderEntity order : lists) {
			try {
				this.orderService.autoCancelWithdrawOrder(order);
			} catch (BusinessException e) {
				XxlJobLogger.log("对公户提现订单超时自动取消失败，订单号：{}，错误原因：{}", order.getOrderNo(), e.getMessage());
			} catch (Exception e) {
				XxlJobLogger.log("对公户提现订单超时自动取消失败，订单号：{}，错误原因：{}", order.getOrderNo(), e.getMessage());
				XxlJobLogger.log(e);
			}
		}
		XxlJobLogger.log("对公户提现订单超时自动取消定时任务任务结束");
		return SUCCESS;
	}
}
