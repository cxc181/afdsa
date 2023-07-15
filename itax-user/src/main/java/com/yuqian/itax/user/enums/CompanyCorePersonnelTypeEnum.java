/**
 * Linkea.cn Inc.
 * Copyright (c) 2010-2012 All Rights Reserved.
 */
package com.yuqian.itax.user.enums;

/**
 *  @Author: lmh
 *  @Date: 2022/6/28
 *  @Description: 企业核心成员成员类型
 */
public enum CompanyCorePersonnelTypeEnum {
	SHAREHOLDER(1, "股东"),
	SUPERVISOR(2, "监事"),
	FINANCE(3, "财务"),
	NO_JOB_SHAREHOLDER(4, "无职务股东");
	private Integer value;
	private String message;

	private CompanyCorePersonnelTypeEnum(Integer value, String message) {
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

	public static CompanyCorePersonnelTypeEnum getByValue(Integer value) {
		for (CompanyCorePersonnelTypeEnum state : values()) {
			if (state.getValue().equals(value)) {
				return state;
			}
		}
		return null;
	}
}


