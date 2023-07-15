/**
 * Linkea.cn Inc.
 * Copyright (c) 2010-2012 All Rights Reserved.
 */
package com.yuqian.itax.order.enums;

/**
 * 工商注销订单状态枚举：0-待付款 1-注销处理中 2-注销成功 3-已取消
 * @author：pengwei
 * @Date：2020/2/17 9:45
 * @version：1.0
 */
public enum CompCancelOrderStatusEnum {
	TO_BE_PAY(0, "待付款"),
	IN_PROCESSING(1, "注销处理中"),
	TAX_CANCEL_COMPLETED(2, "税务注销成功"),
	CANCELED(3, "已取消"),
	BILL_HANDLE(4, "税单待处理"),
	COMPANY_CANCEL_COMPLETED(5, "工商注销成功");
	private Integer value;
	private String message;

	private CompCancelOrderStatusEnum(Integer value, String message) {
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

	public static CompCancelOrderStatusEnum getByValue(Integer value) {
		for (CompCancelOrderStatusEnum state : values()) {
			if (state.getValue().equals(value)) {
				return state;
			}
		}
		return null;
	}
}


