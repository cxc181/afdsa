/**
 * Linkea.cn Inc.
 * Copyright (c) 2010-2012 All Rights Reserved.
 */
package com.yuqian.itax.profits.enums;

/**
 * 工商注册枚举
 * @author：pengwei
 * @Date：2019/12/6 11:12
 * @version：1.0
 */
public enum ProfitsDetailStatusEnum {
	PROFITS_WAIT(0, "待分润"),
	PROFITS_ING(1, "分润中"),
	PROFITS_SETTLEMENT_WAIT(2, "已分润待结算"),
	PROFITS_SETTLEMENT(3, "已分润已结算"),
	CANCELLED(4, "分润失败");
	private Integer value;
	private String message;

	private ProfitsDetailStatusEnum(Integer value, String message) {
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

	public static ProfitsDetailStatusEnum getByValue(Integer value) {
		for (ProfitsDetailStatusEnum state : values()) {
			if (state.getValue().equals(value)) {
				return state;
			}
		}
		return null;
	}
}


