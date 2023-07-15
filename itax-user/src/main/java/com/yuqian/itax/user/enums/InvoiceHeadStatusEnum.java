/**
 * Linkea.cn Inc.
 * Copyright (c) 2010-2012 All Rights Reserved.
 */
package com.yuqian.itax.user.enums;


/**
 * 发票抬头状态枚举类
 * @author：yejian
 * @Date：2019/12/10 16:07
 * @version：1.0
 */
public enum InvoiceHeadStatusEnum {
	UNAVAILABLE(0, "不可用"),
	AVAILABLE(1, "可用");
	private Integer value;
	private String message;

	private InvoiceHeadStatusEnum(Integer value, String message) {
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

	public static InvoiceHeadStatusEnum getByValue(Integer value) {
		for (InvoiceHeadStatusEnum state : values()) {
			if (state.getValue().equals(value)) {
				return state;
			}
		}
		return null;
	}
}


