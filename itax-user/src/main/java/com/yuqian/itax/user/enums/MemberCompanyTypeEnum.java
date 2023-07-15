/**
 * Linkea.cn Inc.
 * Copyright (c) 2010-2012 All Rights Reserved.
 */
package com.yuqian.itax.user.enums;

/**
 *  @Author: Kaven
 *  @Date: 2020/1/13 14:32
 *  @Description: 员工企业类型枚举类
 *  企业类型 1-个体开户 2-个独开户 3-有限合伙 4-有限责任
 */
public enum MemberCompanyTypeEnum {
	INDIVIDUAL(1, "个体开户"),
	INDEPENDENTLY(2, "个独开户"),
	LIMITED_PARTNER(3, "有限合伙"),
	LIMITED_LIABILITY(4, "有限责任");
	private Integer value;
	private String message;

	private MemberCompanyTypeEnum(Integer value, String message) {
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

	public static MemberCompanyTypeEnum getByValue(Integer value) {
		for (MemberCompanyTypeEnum state : values()) {
			if (state.getValue().equals(value)) {
				return state;
			}
		}
		return null;
	}
}


