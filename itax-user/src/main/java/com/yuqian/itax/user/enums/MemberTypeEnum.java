/**
 * Linkea.cn Inc.
 * Copyright (c) 2010-2012 All Rights Reserved.
 */
package com.yuqian.itax.user.enums;

/**
 *  @Author: Kaven
 *  @Date: 2019/12/9 12:22
 *  @Description: 会员类型
 */
public enum MemberTypeEnum {
	MEMBER(1, "会员"),
	EMPLOYEE(2, "员工");
	private Integer value;
	private String message;

	private MemberTypeEnum(Integer value, String message) {
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

	public static MemberTypeEnum getByValue(Integer value) {
		for (MemberTypeEnum state : values()) {
			if (state.getValue().equals(value)) {
				return state;
			}
		}
		return null;
	}
}


