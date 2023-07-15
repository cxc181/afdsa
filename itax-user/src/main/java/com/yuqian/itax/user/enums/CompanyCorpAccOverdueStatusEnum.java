/**
 * Linkea.cn Inc.
 * Copyright (c) 2010-2012 All Rights Reserved.
 */
package com.yuqian.itax.user.enums;

/**
 * 对公户过期状态枚举类
 * @author：LiuMenghao
 * @Date：2021/02/03
 * @version：2.8
 * @Description: 对公户过期状态枚举类 1-正常 2-即将过期 2-已过期
 */

public enum CompanyCorpAccOverdueStatusEnum {
	NORMAL(1, "正常"),
	DUESOON(2, "即将过期"),
	OVERDUE(3, "已过期");

	private Integer value;
	private String message;

	private CompanyCorpAccOverdueStatusEnum(Integer value, String message) {
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

	public static CompanyCorpAccOverdueStatusEnum getByValue(Integer value) {
		for (CompanyCorpAccOverdueStatusEnum state : values()) {
			if (value.equals(state.getValue())) {
				return state;
			}
		}
		return null;
	}
}


