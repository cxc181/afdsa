package com.yuqian.itax.common.constants;

/**
 *  @Author: Kaven
 *  @Date: 2020/4/13 16:31
 *  @Description: 系统错误码常量类
 */
public interface ErrorCodeConstants {

	/* 订单相关错误码常量 */
	/**
	 * 已存在支付中的订单
	 */
	static final Integer EXIST_PAYING_ERROR = 100;

	/**
	 * 不支持的订单渠道
	 */
	static final Integer UN_SUPPORT_PAY_CHANNEL = 101;

	/**
	 * 未收到支付结果
	 */
	Integer REFUND_FAILED = 102;
}
