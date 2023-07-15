/**
 * Linkea.cn Inc.
 * Copyright (c) 2010-2012 All Rights Reserved.
 */
package com.yuqian.itax.order.enums;

/**
 * 个体户、对公户续费订单状态枚举
 * @author：pengwei
 * @Date：2021/2/5 10:09
 * @version：1.0
 * 订单状态 0-待支付 1-支付中 2-已完成 3-已取消
 */
public enum ContOrderStatusEnum {
	TO_BE_PAY(0, "待支付"),
	PAYING(1, "支付中"),
	COMPLETED(2, "已完成"),
	CANCELLED(3, "已取消"),
	;
	private Integer value;
	private String message;

	private ContOrderStatusEnum(Integer value, String message) {
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

	public static ContOrderStatusEnum getByValue(Integer value) {
		for (ContOrderStatusEnum state : values()) {
			if (state.getValue().equals(value)) {
				return state;
			}
		}
		return null;
	}
}


