package com.yuqian.itax.xxljob.jobhandler.pay;

import com.alibaba.fastjson.JSONObject;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import com.xxl.job.core.log.XxlJobLogger;
import com.yuqian.itax.agent.entity.OemEntity;
import com.yuqian.itax.agent.service.OemService;
import com.yuqian.itax.common.util.CollectionUtil;
import com.yuqian.itax.pay.service.PartiallyRepayService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * 代付每日申请额度跑批
 * @author：pengwei
 * @Date：2019年10月23日
 * @version：1.0
 */
@JobHandler(value="partiallyRepayApplyLimitHandler")
@Component
public class PartiallyRepayApplyLimitHandler extends IJobHandler {

	@Autowired
	private PartiallyRepayService partiallyRepayService;

	@Autowired
	private OemService oemService;

	@Override
	public ReturnT<String> execute(String param) throws Exception {
		XxlJobLogger.log("代付申请额度任务启动");
		//查询代付额度
		List<OemEntity> list = oemService.selectAll();
		if (CollectionUtil.isEmpty(list)) {
			XxlJobLogger.log("无oem机构，代付申请额度任务结束");
			return SUCCESS;
		}
		for (OemEntity oemEntity : list) {
			try {
				JSONObject json = partiallyRepayService.merchantBalance(oemEntity.getOemCode());
				if (StringUtils.equals("RPY0000", Optional.ofNullable(json).map(u -> u.getString("p1_resCode")).orElse(null))) {
					//可用余额
					BigDecimal remainAmt = Optional.ofNullable(json).map(u -> u.getBigDecimal("p5_remainAmountAll")).orElse(BigDecimal.ZERO);
					if (new BigDecimal(500).compareTo(remainAmt) < 0 ) {
						XxlJobLogger.log("机构：{}，余额充足：{}", oemEntity.getOemName(), remainAmt);
						continue;
					}
				}
				//申请代付额度
				json = partiallyRepayService.applyLimit(oemEntity.getOemCode(), param);
				if (StringUtils.equals("RPY0000", Optional.ofNullable(json).map(u -> u.getString("p1_resCode")).orElse(null))) {
					XxlJobLogger.log("机构：{}，代付额度申请成功", oemEntity.getOemName());
					continue;
				}
			} catch (Exception e) {
				XxlJobLogger.log("机构：{}，额度申请失败:{} ", oemEntity.getOemName(), e.getMessage());
				XxlJobLogger.log(e);
			}
		}
		XxlJobLogger.log("代付申请额度任务结束");
		return SUCCESS;
	}
}
