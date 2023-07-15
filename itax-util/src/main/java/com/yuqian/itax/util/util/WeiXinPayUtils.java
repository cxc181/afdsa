package com.yuqian.itax.util.util;

import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.DocumentException;

import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

/**
 *  @Author: Kaven
 *  @Date: 2019/12/12 9:54
 *  @Description: 微信支付工具类
 */
@Slf4j
public class WeiXinPayUtils {
	
	/**
	 * 微信下单
	 * 
	 * @param orderId
	 * @param amt
	 * @param goodsName
	 * @param ip
	 * @return
	 * @throws DocumentException
	 */
	public static Map<String, String> placeOrder(String appId, String appSecret, 
			String mchId, String orderId, Long amt, String goodsName, String ip, String notifyUrl)
			throws DocumentException {
		String nonceStr = WeiXinUtils.getNonceStr(); //生成随机数 用于签名
		Map<String, String> signMap = getPlaceOrderSignMap(appId, mchId, orderId, amt, goodsName, ip, nonceStr, notifyUrl); //签名map
		String sign = WeiXinUtils.getSign(signMap, appSecret); //获取签名
		String reqXml = WeiXinRequstXml.place_order_req_xml.toString()
				.replace("{app_id}", appId) //替换随机字符串
				.replace("{mch_id}", mchId) //替换随机字符串
				.replace("{nonce_str}", nonceStr) //替换随机字符串
				.replace("{sign}", sign) //替换签名
				.replace("{body}", goodsName) //替换商品名称
				.replace("{out_trade_no}", orderId) //替换商品订单号
				.replace("{total_fee}", String.valueOf(amt)) //替换下单金额
				.replace("{spbill_create_ip}", ip) //替换用户ip
				.replace("{notify_url}", notifyUrl); //替换通知地址
		log.info("微信下单请求参数：" + reqXml);
		String resultXml = HttpUtil.post("https://api.mch.weixin.qq.com/pay/unifiedorder", reqXml);
		Map<String, String> resultMap = WeiXinUtils.parseXml(resultXml);
		log.info(resultMap.toString());
		String retCode = resultMap.get("return_code");
		String resultCode = resultMap.get("result_code");
		if(StringUtils.equals(retCode, "SUCCESS") && StringUtils.equals(resultCode, "SUCCESS")) { //下单成功
			//下单成功 生成手机端支付map
			Map<String, String> payMap = getPayMap(appId, mchId, resultMap.get("prepay_id"));
			payMap.put("sign", WeiXinUtils.getSign(payMap, appSecret)); //获取签名
			payMap.put("return_code", retCode);
			payMap.put("result_code", resultCode);
			return payMap;
		}
		return resultMap;
	}

	/**
	 * 查询微信订单
	 *
	 * @param orderId
	 * @return
	 */
	public static Map<String, String> queryOrder(String appId, String appSecret, String mchId, String orderId) throws DocumentException {
		String nonceStr = WeiXinUtils.getNonceStr(); //生成随机数 用于签名
		Map<String, String> signMap = getQueryOrderSignMap(appId, mchId, orderId, nonceStr); //生成查询订单签名map
		String sign = WeiXinUtils.getSign(signMap, appSecret); //获取签名
		String reqXml = WeiXinRequstXml.query_order_req_xml.toString().replace("{out_trade_no}", orderId) //替换订单号
													.replace("{sign}", sign) //替换签名
													.replace("{app_id}", appId) //替换随机字符串
													.replace("{mch_id}", mchId) //替换随机字符串
													.replace("{nonce_str}", nonceStr);
		log.info(reqXml);
		String resultXml = HttpUtil.post("https://api.mch.weixin.qq.com/pay/orderquery", reqXml);
		log.info(resultXml);
		Map<String, String> resultMap = WeiXinUtils.parseXml(resultXml);
		log.info(resultMap.toString());
		return resultMap;
	}

	/**
	 * 获取下单签名map
	 *
	 * @param orderId
	 * @param amt
	 * @param goodsName
	 * @param ip
	 * @param nonceStr
	 * @return
	 */
	private static Map<String, String> getPlaceOrderSignMap(String appId, String mchId, String orderId,
			Long amt, String goodsName, String ip, String nonceStr, String notifyUrl) {
		Map<String, String> reqMap = new TreeMap<String, String>();
		reqMap.put("appid", appId); //应用id
		reqMap.put("mch_id", mchId); //商户id
		reqMap.put("device_info", WeiXinUtils.DEVICE_INFO); //设备号
		reqMap.put("nonce_str", nonceStr); //随机字符串
		reqMap.put("body", goodsName); //商品描述
		reqMap.put("out_trade_no", orderId); //商户订单号
		reqMap.put("total_fee", String.valueOf(amt)); //订单总金额，单位为分
		reqMap.put("spbill_create_ip", ip); //用户端实际ip
		reqMap.put("notify_url", notifyUrl); //通知地址
		reqMap.put("trade_type", WeiXinUtils.TRADE_TYPE); //支付类型
		return reqMap;
	}

	/**
	 * 获取查询订单签名map
	 *
	 * @param appId 微信应用id
	 * @param mchId 支付商户号
	 * @param orderId 订单号
	 * @param nonceStr 随机码
	 * @return
	 */
	private static Map<String, String> getQueryOrderSignMap(String appId, String mchId, String orderId, String nonceStr) {
		Map<String, String> reqMap = new TreeMap<String, String>();
		reqMap.put("appid", appId); //应用id
		reqMap.put("mch_id", mchId); //商户id
		reqMap.put("out_trade_no", orderId); //设备号
		reqMap.put("nonce_str", nonceStr); //随机字符串
		return reqMap;
	}

	/**
	 * 生成支付Map
	 * @param prepayid
	 * @return
	 */
	private static Map<String, String> getPayMap(String appId, String mchId, String prepayid) {
		Map<String, String> payMap = new TreeMap<String, String>();
		payMap.put("appid", appId); //应用ID
		payMap.put("partnerid", mchId); //商户号
		payMap.put("prepayid", prepayid);
		payMap.put("package", WeiXinUtils.PAY_PACKAGE);
		payMap.put("noncestr", WeiXinUtils.getNonceStr());
		payMap.put("timestamp", String.valueOf(System.currentTimeMillis()).substring(0, 10));
		return payMap;
	}
	
	public static void main(String[] args){
		try {
			Map<String, String> queryOrder = queryOrder("wx4533528c896b9f42", "123456789123456789123456789posss", "1507083671", "20181017130750001");
			System.out.println(JSON.toJSON(queryOrder));
		}catch (Exception e){
			log.error(e.getMessage());
		}

	}
}
