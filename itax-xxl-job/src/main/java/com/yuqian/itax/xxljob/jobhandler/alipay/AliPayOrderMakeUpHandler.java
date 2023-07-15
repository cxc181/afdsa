package com.yuqian.itax.xxljob.jobhandler.alipay;

import com.alibaba.fastjson.JSONObject;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import com.xxl.job.core.log.XxlJobLogger;
import com.yuqian.itax.agent.service.OemParamsService;
import com.yuqian.itax.agent.service.OemService;
import com.yuqian.itax.common.util.CollectionUtil;
import com.yuqian.itax.message.enums.MessageEnum;
import com.yuqian.itax.order.service.OrderService;
import com.yuqian.itax.order.service.RegisterOrderService;
import com.yuqian.itax.pay.entity.PayWaterEntity;
import com.yuqian.itax.pay.enums.PayWayEnum;
import com.yuqian.itax.pay.service.PayWaterService;
import com.yuqian.itax.util.entity.AliPayDto;
import com.yuqian.itax.util.util.AliPayUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @Description 支付宝支付订单补单调度任务
 * @Author  Kaven
 * @Date   2020/10/23 14:01
*/
@Slf4j
@JobHandler(value="aliPayOrderMakeUpHandler")
@Component
public class AliPayOrderMakeUpHandler extends IJobHandler {

	@Autowired
	private OrderService orderService;
	@Autowired
	private RegisterOrderService registerOrderService;
	@Autowired
	private PayWaterService payWaterService;
	@Autowired
	private OemService oemService;
	@Autowired
	private OemParamsService oemParamsService;

	@Override
	public ReturnT<String> execute(String param) throws Exception {
		XxlJobLogger.log("支付宝支付订单补单调度任务启动");

		// 查询业务系统支付中状态的支付宝支付订单流水列表
		List<PayWaterEntity> list = payWaterService.selectPayingList(PayWayEnum.ALIPAY.getValue());
		if (CollectionUtil.isEmpty(list)) {
			XxlJobLogger.log("无支付中的支付宝支付订单，补单调度任务结束");
			return SUCCESS;
		}
		// 请求渠道订单查询接口
		for (PayWaterEntity payWater : list) {
			try {
				AliPayDto payDto = this.registerOrderService.buildAliPayParams(payWater);
				Map<String,Object> dataMap = AliPayUtils.queryAliOrder(payDto);

				log.info("订单查询接口返回结果：{}", JSONObject.toJSONString(dataMap));
				XxlJobLogger.log("订单查询接口返回结果：{}", JSONObject.toJSONString(dataMap));

				// 解析返回结果
				String retCode = dataMap.get("code").toString();// 返回码
				String retMsg = dataMap.get("msg").toString();// 返回信息

				if (StringUtils.equals(retCode, "00")) {
					Date payDate = new Date();
					JSONObject dataObj = (JSONObject)dataMap.get("data");
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
		XxlJobLogger.log("支付宝支付订单补单调度任务结束");
		return SUCCESS;
	}

}
