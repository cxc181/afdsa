/**
 * Linkea.cn Inc.
 * Copyright (c) 2010-2012 All Rights Reserved.
 */
package com.yuqian.itax.order.enums;

/**
 * @Description 钱包类型枚举：1-消费钱包 2-佣金钱包
 * @Author  Kaven
 * @Date   2020/6/23 12:06 下午
*/
public enum WalletTypeEnum {
	CONSUMER_WALLET(1, "消费钱包"),
	COMMISSION_WALLET(2, "佣金钱包"),
	OTHER(3, "其他");
	private Integer value;
	private String message;

	private WalletTypeEnum(Integer value, String message) {
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

	public static WalletTypeEnum getByValue(Integer value) {
		for (WalletTypeEnum state : values()) {
			if (state.getValue().equals(value)) {
				return state;
			}
		}
		return null;
	}
}


