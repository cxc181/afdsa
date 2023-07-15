/**
 * Linkea.cn Inc.
 * Copyright (c) 2010-2012 All Rights Reserved.
 */
package com.yuqian.itax.coupons.entity.enums;

/**
 * 优惠卷发放记录状态枚举
 * @author：HZ
 * @Date：2020/4/12 11:12
 * @version：1.0
 */
public enum CouponsIssueRecordStatusEnum {
	UNUSED(0,"未使用"),
	USED(1, "已使用"),
	STALE(2, "已过期"),
	WITHDRAWN(3, "已撤回");
	private Integer value;
	private String message;

	private CouponsIssueRecordStatusEnum(Integer value, String message) {
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

	public static CouponsIssueRecordStatusEnum getByValue(Integer value) {
		for (CouponsIssueRecordStatusEnum state : values()) {
			if (state.getValue().equals(value)) {
				return state;
			}
		}
		return null;
	}
}


