package com.yuqian.itax.xxljob.jobhandler.wechatpay;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import com.xxl.job.core.log.XxlJobLogger;
import com.yuqian.itax.common.util.CollectionUtil;
import com.yuqian.itax.message.enums.MessageEnum;
import com.yuqian.itax.order.service.OrderService;
import com.yuqian.itax.order.service.RegisterOrderService;
import com.yuqian.itax.pay.entity.PayWaterEntity;
import com.yuqian.itax.pay.enums.PayWayEnum;
import com.yuqian.itax.pay.service.PayWaterService;
import com.yuqian.itax.util.entity.WechatPayDto;
import com.yuqian.itax.util.util.WechatPayUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 *  @Author: Kaven
 *  @Date: 2020/1/16 10:48
 *  @Description: 微信支付订单补单调度任务
 */
@Slf4j
@JobHandler(value="wechatPayOrderMakeUpHandler")
@Component
public class WechatPayOrderMakeUpHandler extends IJobHandler {

	@Autowired
	private OrderService orderService;
	@Autowired
	private RegisterOrderService registerOrderService;
	@Autowired
	private PayWaterService payWaterService;

	@Override
	public ReturnT<String> execute(String param) throws Exception {
		XxlJobLogger.log("微信支付订单补单调度任务启动");

		// 查询业务系统支付中状态的微信支付订单流水列表
		List<PayWaterEntity> list = payWaterService.selectPayingList(PayWayEnum.WECHATPAY.getValue());
		if (CollectionUtil.isEmpty(list)) {
			XxlJobLogger.log("无支付中的微信支付订单，补单调度任务结束");
			return SUCCESS;
		}
		// 请求渠道订单查询接口
		for (PayWaterEntity payWater : list) {
			try {
				WechatPayDto payDto = this.registerOrderService.buildWechatParams(payWater);
				Map<String,Object> dataMap = WechatPayUtils.queryWxOrder(payDto);

				log.info("订单查询接口返回结果：{}", JSONObject.toJSONString(dataMap));
				XxlJobLogger.log("订单查询接口返回结果：{}", JSONObject.toJSONString(dataMap));

				// 解析返回结果
				String retCode = dataMap.get("code").toString();// 返回码
				String retMsg = dataMap.get("msg").toString();// 返回信息

				if (StringUtils.equals(retCode, "00")) {
					Date payDate = new Date();
					JSONObject dataObj = new JSONObject();
					if("1".equals(payDto.getChannel())){
						dataObj = (JSONObject)dataMap.get("data");
					}else {
						JSONArray dataArrays = (JSONArray) dataMap.get("data");
						if (dataArrays.size() > 0) {
							dataObj = dataArrays.getJSONObject(0);
						}
					}
					//（-1-交易失败", 0-交易初始化, 1-交易处理中, 3-交易超时,4-交易关闭,5-交易成功）
					String orderStatus = dataObj.getString("status");
					if("5".equals(orderStatus)) { // 订单支付成功
						// 更新订单状态
						this.orderService.updateOrderStatus(payWater.getPayNo(), null, payDate, MessageEnum.SUCCESS.getValue(), orderStatus,retMsg);
					}else if("0".equals(orderStatus) || "1".equals(orderStatus)){
						continue;// 不做处理，直接退出
					} else {//订单支付失败
						// 更新订单状态
						this.orderService.updateOrderStatus(payWater.getPayNo(), null, payDate, MessageEnum.SYSTEM_ERROR.getValue(), orderStatus, retMsg);
					}
				}
			} catch (Exception e) {
				XxlJobLogger.log("订单：{}，补单调度失败:{} ", payWater.getOrderNo(), e.getMessage());
				XxlJobLogger.log(e);
			}
		}
		XxlJobLogger.log("微信支付订单补单调度任务结束");
		return SUCCESS;
	}

}
