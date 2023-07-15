/**
 * Linkea.cn Inc.
 * Copyright (c) 2010-2012 All Rights Reserved.
 */
package com.yuqian.itax.order.enums;

/**
 *  @Author: Kaven
 *  @Date: 2020/2/14 9:29
 *  @Description: 公司注销订单状态枚举类：0-待付款 1-注销处理中 2-注销成功 3-已取消
 */
public enum QYZXOrderStatusEnum {
	TO_BE_PAID(0, "待付款"),
	CANCEL_HANDLEING(1, "注销处理中"),
	CANCEL_SUCCESS(2, "注销成功"),
	CANCELED(3, "已取消"),
	TO_BE_TAX_BILL(4, "税单待处理")
	;
	private Integer value;
	private String message;

	private QYZXOrderStatusEnum(Integer value, String message) {
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

	public static QYZXOrderStatusEnum getByValue(Integer value) {
		for (QYZXOrderStatusEnum state : values()) {
			if (state.getValue().equals(value)) {
				return state;
			}
		}
		return null;
	}
}


