package com.yuqian.itax.util.util;

/**
 * 微信请求xml
 * @author LiuXianTing
 *
 */
public class WeiXinRequstXml {
	
	/**
	 * 下单xml
	 */
	protected static StringBuffer place_order_req_xml = new StringBuffer();
	
	/**
	 * 查询订单请求xml
	 */
	protected static StringBuffer query_order_req_xml = new StringBuffer();
	
	static {
		//初始化 下单xml
		place_order_req_xml.append("<xml>");
		place_order_req_xml.append("<appid>{app_id}</appid>"); //应用id
		place_order_req_xml.append("<mch_id>{mch_id}</mch_id>"); //商户id
		place_order_req_xml.append("<device_info>").append(WeiXinUtils.DEVICE_INFO).append("</device_info>"); //设备号
		place_order_req_xml.append("<nonce_str>{nonce_str}</nonce_str>"); //随机字符串
		place_order_req_xml.append("<sign>{sign}</sign>"); //签名
		place_order_req_xml.append("<body>{body}</body>"); //商品描述
		place_order_req_xml.append("<out_trade_no>{out_trade_no}</out_trade_no>"); //商户订单号
		place_order_req_xml.append("<total_fee>{total_fee}</total_fee>"); //订单总金额，单位为分
		place_order_req_xml.append("<spbill_create_ip>{spbill_create_ip}</spbill_create_ip>"); //用户端实际ip
		place_order_req_xml.append("<notify_url>{notify_url}</notify_url>"); //通知地址
		place_order_req_xml.append("<trade_type>").append(WeiXinUtils.TRADE_TYPE).append("</trade_type>"); //支付类型
		place_order_req_xml.append("</xml>");
		//初始化查询订单xml
		query_order_req_xml.append("<xml>");
		query_order_req_xml.append("<appid>{app_id}</appid>"); //应用id
		query_order_req_xml.append("<mch_id>{mch_id}</mch_id>"); //商户id
		query_order_req_xml.append("<nonce_str>{nonce_str}</nonce_str>"); //随机字符串
		query_order_req_xml.append("<sign>{sign}</sign>"); //签名
		query_order_req_xml.append("<out_trade_no>{out_trade_no}</out_trade_no>"); //订单号
		query_order_req_xml.append("</xml>");
	}
}
