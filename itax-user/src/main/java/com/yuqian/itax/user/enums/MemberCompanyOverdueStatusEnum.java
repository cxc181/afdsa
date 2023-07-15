/**
 * Linkea.cn Inc.
 * Copyright (c) 2010-2012 All Rights Reserved.
 */
package com.yuqian.itax.user.enums;

/**
 * 我的企业过期状态枚举类
 * @author：LiuMenghao
 * @Date：2021/02/03
 * @version：2.8
 * @Description: 我的企业过期状态枚举类 1-正常 2-即将过期 2-已过期
 */

public enum MemberCompanyOverdueStatusEnum {
	NORMAL(1, "正常"),
	DUESOON(2, "即将过期"),
	OVERDUE(3, "已过期");

	private Integer value;
	private String message;

	private MemberCompanyOverdueStatusEnum(Integer value, String message) {
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

	public static MemberCompanyOverdueStatusEnum getByValue(Integer value) {
		for (MemberCompanyOverdueStatusEnum state : values()) {
			if (value.equals(state.getValue())) {
				return state;
			}
		}
		return null;
	}
}


