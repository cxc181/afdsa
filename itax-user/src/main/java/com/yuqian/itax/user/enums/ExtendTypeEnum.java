/**
 * Linkea.cn Inc.
 * Copyright (c) 2010-2012 All Rights Reserved.
 */
package com.yuqian.itax.user.enums;

/**
 *  @Author: Kaven
 *  @Date: 2020/6/3 9:17
 *  @Description: 推广类型 1-散客 2-直客 3-顶级直客
 */
public enum ExtendTypeEnum {
	INDEPENDENT_CUSTOMER(1, "散客"),
	STRAIGHT_CUSTOMER(2, "直客"),
	TOP_STRAIGHT_CUSTOMER(3, "顶级直客"),;
	private Integer value;
	private String message;

	private ExtendTypeEnum(Integer value, String message) {
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

	public static ExtendTypeEnum getByValue(Integer value) {
		for (ExtendTypeEnum state : values()) {
			if (state.getValue().equals(value)) {
				return state;
			}
		}
		return null;
	}
}


