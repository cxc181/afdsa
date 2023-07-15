package com.yuqian.itax.xxljob.jobhandler.bytedance;

import com.alibaba.fastjson.JSONObject;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import com.xxl.job.core.log.XxlJobLogger;
import com.yuqian.itax.agent.entity.OemParamsEntity;
import com.yuqian.itax.agent.service.OemParamsService;
import com.yuqian.itax.common.base.exception.BusinessException;
import com.yuqian.itax.common.util.CollectionUtil;
import com.yuqian.itax.message.enums.MessageEnum;
import com.yuqian.itax.order.service.OrderService;
import com.yuqian.itax.pay.entity.PayWaterEntity;
import com.yuqian.itax.pay.enums.PayWayEnum;
import com.yuqian.itax.pay.service.PayWaterService;
import com.yuqian.itax.util.util.BytedanceUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 *  @Author: 蒋匿
 *  @Date: 2021/9/24
 *  @Description: 字节跳动支付订单查询调度任务
 */
@Slf4j
@JobHandler(value="bytedancePayOrderQueryHandler")
@Component
public class BytedancePayOrderQueryHandler extends IJobHandler {

	@Autowired
	private OrderService orderService;
	@Autowired
	private PayWaterService payWaterService;
	@Autowired
	private OemParamsService oemParamsService;

	@Override
	public ReturnT<String> execute(String param) throws Exception {
		XxlJobLogger.log("字节跳动支付订单补查询单调度任务启动");

		// 查询业务系统支付中状态的微信支付订单流水列表
		List<PayWaterEntity> list = payWaterService.selectPayingList(PayWayEnum.BYTEDANCE.getValue());
		if (CollectionUtil.isEmpty(list)) {
			XxlJobLogger.log("无支付中的字节跳动支付订单，补单调度任务结束");
			return SUCCESS;
		}
		// 请求渠道订单查询接口
		for (PayWaterEntity payWater : list) {
			try {
				//读取渠道支付相关配置
				OemParamsEntity paramsEntity = this.oemParamsService.getParams(payWater.getOemCode(),30);
				if(null == paramsEntity){
					throw new BusinessException("未配置渠道支付相关信息！");
				}
				JSONObject params = JSONObject.parseObject(paramsEntity.getParamsValues());
				Map<String,Object> dataMap = BytedanceUtils.queryBytedancePayOrder(paramsEntity.getAccount(),payWater.getPayNo(),params.getString("paySalt"));

				log.info("订单查询接口返回结果：{}", JSONObject.toJSONString(dataMap));
				XxlJobLogger.log("订单查询接口返回结果：{}", JSONObject.toJSONString(dataMap));

				// 解析返回结果
				String retCode = dataMap.get("code").toString();// 返回码
				String retMsg = dataMap.get("msg").toString();// 返回信息

				if (StringUtils.equals(retCode, "0")) {
					Date payDate = new Date();
					JSONObject dataObj = (JSONObject)dataMap.get("data");
					//（"order_status": "PROCESSING-处理中|SUCCESS-成功|FAIL-失败|TIMEOUT-超时"）
					String orderStatus = dataObj.getString("order_status");
					if("SUCCESS".equals(orderStatus)) { // 订单支付成功
						// 更新订单状态
						this.orderService.updateOrderStatus(payWater.getPayNo(), null, payDate, MessageEnum.SUCCESS.getValue(), orderStatus,retMsg);
					}else if("FAIL".equals(orderStatus) || "TIMEOUT".equals(orderStatus)){//订单支付失败
						// 更新订单状态
						this.orderService.updateOrderStatus(payWater.getPayNo(), null, payDate, MessageEnum.SYSTEM_ERROR.getValue(), orderStatus, retMsg);
					} else {
						continue;// 不做处理，直接退出
					}
				}
			} catch (Exception e) {
				XxlJobLogger.log("订单：{}，字节跳动订单查询失败:{} ", payWater.getOrderNo(), e.getMessage());
				XxlJobLogger.log(e);
			}
		}
		XxlJobLogger.log("字节跳动支付订单补单调度任务结束");
		return SUCCESS;
	}

}
