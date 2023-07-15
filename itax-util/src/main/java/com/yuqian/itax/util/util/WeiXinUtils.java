package com.yuqian.itax.util.util;

import cn.hutool.core.collection.CollectionUtil;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.math.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import java.util.*;

/**
 *  @Author: Kaven
 *  @Date: 2019/12/12 9:57
 *  @Description: 微信支付相关工具类
 */
public class WeiXinUtils {
	
	/**
	 * 设备号
	 */
	public static final String DEVICE_INFO = "WEB";
	
	/**
	 * 交易类型
	 */
	public static final String TRADE_TYPE = "APP";
	
	/**
	 * 支付扩展字段
	 */
	public static final String PAY_PACKAGE = "Sign=WXPay";
	
	/**
	 * xml头部
	 */
	protected static final String XML_HEAD = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
	
	/**
	 * 生成随机签名
	 * md5（sha1（uuid+8位随机英文数字））
	 * 
	 * @return
	 */
	public static String getNonceStr() {
		return Md5Util.signature(DigestUtils.sha1Hex(UUID.randomUUID()+ String.valueOf(RandomUtils.nextInt(8))));
	}

	/**
	 * 生成签名
	 *
	 * @param signMap
	 * @return
	 */
	public static String getSign(Map<String, String> signMap, String appSecret) {
		StringBuffer sb = new StringBuffer();
		Set<String> keySet = signMap.keySet();
		for(String key : keySet) {
			String value = signMap.get(key);
			if(StringUtils.isBlank(value) || StringUtils.equals(key, "sign")) {//值为空不参与签名
				continue;
			}
			sb.append(key).append("=").append(signMap.get(key)).append("&");
		}
		sb.append("key=").append(appSecret);
		return Md5Util.signature(sb.toString()).toUpperCase();
	}
	
	/**
	 * 解析返回结果xml 并封装成map
	 * @param xmlStr
	 * @return
	 * @throws DocumentException
	 */
	@SuppressWarnings("unchecked")
	public static Map<String, String> parseXml(String xmlStr) throws DocumentException {
		xmlStr = XML_HEAD + xmlStr;
		Document document = DocumentHelper.parseText(xmlStr);
		Element rootElement = document.getRootElement();
		List<Element> childList = rootElement.elements();
		Map<String, String> xmlMap = new TreeMap<String, String>();
		if(CollectionUtil.isNotEmpty(childList)) {
			for(Element child : childList) {
				xmlMap.put(child.getName(), child.getStringValue());
			}
		} 
		return xmlMap;
	}
	
	/**
	 * 微信判断参数的合法性
	 * 
	 * @param appId
	 * @param mchId
	 * @param weixinNotifyMap
	 * @return wxRetMap 验证失败 返回失败信息 验证成功 返回空map
	 */
	public static String validateMap(String appId, String mchId, Map<String, String> weixinNotifyMap) {
		if(!StringUtils.equals(weixinNotifyMap.get("appid").trim(), appId)) { //应用id
			return getFailRetStr("appid错误");
		}
		if(!StringUtils.equals(weixinNotifyMap.get("mch_id").trim(), mchId)) { //应用id
			return getFailRetStr("商户号错误");
		}
		if(StringUtils.isBlank(weixinNotifyMap.get("nonce_str").trim())) { //随机字符串
			return getFailRetStr("随机字符串为空");
		}
		if(StringUtils.isBlank(weixinNotifyMap.get("sign").trim())) { //签名
			return getFailRetStr("签名为空");
		}
		String result_code = weixinNotifyMap.get("result_code").trim();
		if(StringUtils.isBlank(result_code) || !StringUtils.containsAny(result_code, "SUCCESS", "FAIL")) { //签名
			return getFailRetStr("业务结果错误");
		}
		String return_code = weixinNotifyMap.get("return_code").trim();
		if(StringUtils.isBlank(return_code) || !StringUtils.containsAny(return_code, "SUCCESS", "FAIL")) { //签名
			return getFailRetStr("返回状态码错误");
		}
		if(StringUtils.isBlank(weixinNotifyMap.get("openid").trim())) {
			return getFailRetStr("用户标志不能为空");
		}
		if(!StringUtils.equals("APP", weixinNotifyMap.get("trade_type").trim())) {
			return getFailRetStr("交易类型错误");
		}
		if(StringUtils.isBlank(weixinNotifyMap.get("bank_type").trim())) {
			return getFailRetStr("付款银行不能为空");
		}
		if(StringUtils.isBlank(weixinNotifyMap.get("total_fee").trim())) {
			return getFailRetStr("订单总金额不能为空");
		}
		if(StringUtils.isBlank(weixinNotifyMap.get("cash_fee").trim())) {
			return getFailRetStr("现金支付金额不能为空");
		}
		if(StringUtils.isBlank(weixinNotifyMap.get("transaction_id").trim())) {
			return getFailRetStr("微信支付订单号不能为空");
		}
		if(StringUtils.isBlank("out_trade_no")) {
			return getFailRetStr("商户订单号不能为空");
		}
		if(StringUtils.isBlank("time_end")) {
			return getFailRetStr("支付完成时间不能为空");
		}
		return null;
	}
	
	public static String getFailRetStr(String failMsg) {
		return getRetStr(false, failMsg);
	}
	
	public static String getSuccessRetStr(String successMsg) {
		return getRetStr(true, successMsg);
	}
	
	/**
	 * 返回错误map
	 *
	 * @param success
	 * @param msg
	 * @return
	 */
	private static String getRetStr(boolean success, String msg) {
		StringBuffer sb = new StringBuffer();
		sb.append("<xml>");
		sb.append("<return_code><![CDATA[SUCCESS]]></return_code>");
		sb.append("<return_msg><![CDATA[OK]]></return_msg>");
		sb.append("</xml>");
		return sb.toString();
	}
}
