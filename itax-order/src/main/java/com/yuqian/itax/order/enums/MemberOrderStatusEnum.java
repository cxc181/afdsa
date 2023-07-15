/**
 * Linkea.cn Inc.
 * Copyright (c) 2010-2012 All Rights Reserved.
 */
package com.yuqian.itax.order.enums;

/**
 * 会员升级订单状态   0-待支付 1-支付中 2-财务审核 3-已完成 4-已取消
 * @author：pengwei
 * @Date：2019/12/12 14:12
 * @version：1.0
 */
public enum MemberOrderStatusEnum {
	TO_BE_PAY(0, "待支付"),
	PAYING(1, "支付中"),
	TO_BE_AUDIT(2, "财务审核"),
	COMPLETED(3, "已完成"),
	CANCELLED(4, "已取消");
	private Integer value;
	private String message;

	private MemberOrderStatusEnum(Integer value, String message) {
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

	public static MemberOrderStatusEnum getByValue(Integer value) {
		for (MemberOrderStatusEnum state : values()) {
			if (state.getValue().equals(value)) {
				return state;
			}
		}
		return null;
	}
}


