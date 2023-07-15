/**
 * Linkea.cn Inc.
 * Copyright (c) 2010-2012 All Rights Reserved.
 */
package com.yuqian.itax.order.enums;

/**
 * 充值提现状态   充值提现：0-待提交 1-支付、提现中，2-支付、提现完成，3-支付、提现失败 4-订单过期  5-已取消 6-待财务审核 7-财务审核失败
 * @author：pengwei
 * @Date：2019/12/12 14:12
 * @version：1.0
 */
public enum RACWStatusEnum {
	TO_SUBMIT(0, "待提交"),
	PAYING(1, "支付、提现中"),
	PAYED(2, "支付、提现完成"),
	PAY_FAILURE(3, "支付、提现失败"),
	EXPIRED(4, "订单过期"),
	CANCELED(5, "已取消"),
	WAIT_FOR_AUDIT(6, "待财务审核"),
	AUDIT_FAIL(7, "财务审核失败"),
	;
	private Integer value;
	private String message;

	private RACWStatusEnum(Integer value, String message) {
		this.value = value;
		this.message = message;
	}

	public Integer getValue() {
		return value;
	}

	public void setValue(Integer value) {
		this.value = value;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public static RACWStatusEnum getByValue(Integer value) {
		for (RACWStatusEnum state : values()) {
			if (state.getValue().equals(value)) {
				return state;
			}
		}
		return null;
	}
}


