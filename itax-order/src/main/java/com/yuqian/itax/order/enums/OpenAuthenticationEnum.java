/**
 * Linkea.cn Inc.
 * Copyright (c) 2010-2012 All Rights Reserved.
 */
package com.yuqian.itax.order.enums;

/**
 * 是否已开启身份验证 0-未开启 1-已开启
 * @author：pengwei
 * @Date：2020/9/25 14:55
 * @version：1.0
 */
public enum OpenAuthenticationEnum {
	CLOSED(0, "未开启"),
	OPENED(1, "已开启"),
	;
	private Integer value;
	private String message;

	private OpenAuthenticationEnum(Integer value, String message) {
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

	public static OpenAuthenticationEnum getByValue(Integer value) {
		for (OpenAuthenticationEnum state : values()) {
			if (state.getValue().equals(value)) {
				return state;
			}
		}
		return null;
	}
}


