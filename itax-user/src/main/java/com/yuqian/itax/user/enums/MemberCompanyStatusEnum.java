/**
 * Linkea.cn Inc.
 * Copyright (c) 2010-2012 All Rights Reserved.
 */
package com.yuqian.itax.user.enums;

/**
 * 我的企业状态枚举类
 * @author：pengwei
 * @Date：2019/12/10 16:07
 * @version：1.0
 */
public enum MemberCompanyStatusEnum {
	NORMAL(1, "正常"),
	PROHIBIT(2, "冻结"),
	//OVERDUE(3, "过期"), //v2.8去除该状态选项，使用过期状态中的已过期选项替代
	TAX_CANCELLED(4, "已税务注销"),
	CANCELLING(5, "注销中"),
	COMPANY_CANCELLED(6, "已工商注销");
	private Integer value;
	private String message;

	private MemberCompanyStatusEnum(Integer value, String message) {
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

	public static MemberCompanyStatusEnum getByValue(Integer value) {
		for (MemberCompanyStatusEnum state : values()) {
			if (value.equals(state.getValue())) {
				return state;
			}
		}
		return null;
	}
}


