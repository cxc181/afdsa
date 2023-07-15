/**
 * Linkea.cn Inc.
 * Copyright (c) 2010-2012 All Rights Reserved.
 */
package com.yuqian.itax.order.enums;

/**
 * @Description
 * @Author  Kaven
 * @Date   2020/6/23 12:06 下午
*/
public enum PayTypeInvoiceEnum {
	ONLINE_PAY(1, "线上支付"),
	OFFLINE_PAY(2, "线下支付"),
	;
	private Integer value;
	private String message;

	private PayTypeInvoiceEnum(Integer value, String message) {
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

	public static PayTypeInvoiceEnum getByValue(Integer value) {
		for (PayTypeInvoiceEnum state : values()) {
			if (state.getValue().equals(value)) {
				return state;
			}
		}
		return null;
	}
}


