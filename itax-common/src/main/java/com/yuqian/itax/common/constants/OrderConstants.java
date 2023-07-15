package com.yuqian.itax.common.constants;

/**
 * @Author Kaven
 * @Description 此类用于定义订单详细信息属性常量,调用交易接口所传的字符串参数名称必须使用些类定义的属性名称
 * @Date 15:46 2019/8/14
 * @Param
 * @return
 **/
public abstract class OrderConstants {

	private OrderConstants() {
	}

	public static final String MQ_PREFIX = "itax-";// MQ队列名前缀

	/**
	 *  向推广平台发送MQ
	 */
	public static final String QUEUE_CASH_ORDER_S = "cash-order-s-queue";

	// S端设备激活通知队列
	public static final String QUEUE_ACTIVITE_ORDER_S = "activite-order-s-queue";

	// 渠道端会员信息通知队列
	public static final String QUEUE_MEMBER_INFO = "user-info-queue";

	/**
	 * 手机号码属性名称
	 */
	public static final String MOBILE_NUMBER = "mobileNumber";

	/**
	 * 付款卡号
	 */
	public static final String PAYING_CARD_NO = "payCardNo";

	/**
	 * POS序列号
	 */
	public static final String PSAM_NO = "psamNo";

	/**
	 * 富友流水号
	 */
	public static final String REF_NO = "refNo";

	/**
	 * 结果码
	 */
	public static final String RESULT_CODE = "resultCode";

	/**
	 * 交易流水
	 */
	public static final String TRADE_NO = "tradeNo";

	/**
	 * 交易订单信息
	 */
	public static final String TRADE_ORDER = "tradeOrder";

	/**
	 * 交易代码
	 */
	public static final String TRADE_CODE = "tradeCode";

	/**
	 * 收款卡号
	 */
	public static final String RECEIVABLE_CARD_NO = "toCardNo";

	/**
	 * epos序列号
	 */
	public static final String EPOS_ID = "eposId";

	/**
	 * 磁道信息
	 */
	public static final String TRACK_DATA = "trackData";

	/**
	 * 加密密码信息
	 */
	public static final String CARD_PIN = "cardPin";

	/**
	 * 商户号
	 */
	public static final String MERCHANT_NO = "merchantNo";

	/**
	 * 会员号
	 */
	public static final String MEMBER_NO = "memberNo";

	/**
	 * 支付密码
	 */
	public static final String PAYMENT_PWD = "paymentPwd";

	/**
	 * 银行名称
	 */
	public static final String BANK_NAME = "bankName";

	/**
	 * 银行名称
	 */
	public static final String SUB_BANK_CODE = "sub_bank_code";

	/**
	 * 持卡人
	 */
	public static final String BANK_CARD_HOLDER = "cardHolder";

	/**
	 * 银行卡卡号
	 */
	public static final String BANK_CARD_NO = "cardNo";

	/**
	 * 支付帐户号
	 */
	public static final String ACCOUNT_NO = "accountNo";

	/**
	 * POS加密密码
	 */
	public static final String POS_ENC_PIN = "encPin";

	/**
	 * POS加密磁道2信息
	 */
	public static final String POS_ENC_TRACK2 = "encTrack2";

	/**
	 * POS加密磁道3信息
	 */
	public static final String POS_ENC_TRACK3 = "encTrack3";

	/**
	 * 磁道2长度
	 */
	public static final String POS_TRACK2_LENGTH = "track2Length";

	/**
	 * 磁道3长度
	 */
	public static final String POS_TRACK3_LENGTH = "track3Length";

	/**
	 * IPOS工作密钥
	 */
	public static final String IPOS_ENC_WORKING_KEY = "encWorkingKey";
	/**
	 * 卡序列号 IC卡专用数据域,F23
	 */
	public static final String IC_CARD_SEQ_NO = "icCardSeqNo";
	/**
	 * IC卡 IC卡专用数据域,F55
	 */
	public static final String IC_DATA = "icData";
	/**
	 * 过期日期
	 */
	public static final String EXPIRY_DATE = "expiryDate";

	/**
	 * KSN
	 */
	public static final String IPOS_KSN = "ksn";

	/**
	 * 会员交易时的经度信息
	 */
	public static final String BIZ_PARAM_LON = "lon";

	/**
	 * 会员交易时的维度信息
	 */
	public static final String BIZ_PARAM_LAT = "lat";

	/**
	 * 会员交易时的经纬信息：112.990921,28.214332
	 */
	public static final String MEMBER_COORDINATES = "coordinates";
	
	/**
	 * 交易事由
	 */
	public static final String INDUSTRY_CODE = "industryCode";

}
