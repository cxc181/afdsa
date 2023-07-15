/**
 * Linkea.cn Inc.
 * Copyright (c) 2010-2012 All Rights Reserved.
 */
package com.yuqian.itax.user.enums;

/**
 *  @Description: 企业纳税人类型枚举类
 *  纳税人类型 1-小规模纳税人 2-一般纳税人
 */
public enum CompanyTaxPayerTypeEnum {
	SMALL_SCALE_TAXPAYER(1, "小规模纳税人"),
	GENERAL_TAXPAYER(2, "一般纳税人");
	private Integer value;
	private String message;

	private CompanyTaxPayerTypeEnum(Integer value, String message) {
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

	public static CompanyTaxPayerTypeEnum getByValue(Integer value) {
		for (CompanyTaxPayerTypeEnum state : values()) {
			if (state.getValue().equals(value)) {
				return state;
			}
		}
		return null;
	}
}


