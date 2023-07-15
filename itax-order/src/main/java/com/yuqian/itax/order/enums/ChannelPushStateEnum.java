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
public enum ChannelPushStateEnum {
	TO_BE_PAY(0, "待推送"),
	PAYING(1, "推送中"),
	TO_BE_AUDIT(2, "已推送"),
	COMPLETED(3, "推送失败"),
	CANCELLED(4, "无需推送");
	private Integer value;
	private String message;

	private ChannelPushStateEnum(Integer value, String message) {
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

	public static ChannelPushStateEnum getByValue(Integer value) {
		for (ChannelPushStateEnum state : values()) {
			if (state.getValue().equals(value)) {
				return state;
			}
		}
		return null;
	}
}


