/**
 * Linkea.cn Inc.
 * Copyright (c) 2010-2012 All Rights Reserved.
 */
package com.yuqian.itax.user.enums;

/**
 *  @Author: Kaven
 *  @Date: 2019/12/9 12:22
 *  @Description: 会员等级枚举类  0-普通会员  1-VIP 2-白银会员 3-税务顾问 4-铂金会员 5-城市服务商
 */
public enum MemberLevelEnum {
	NORMAL(0, "普通用户"),
	BRONZE(1, "VIP"),
	GOLD(3, "税务顾问"),
	DIAMOND(5, "城市服务商"),
	MEMBER(-1, "员工");
	private Integer value;
	private String message;

	private MemberLevelEnum(Integer value, String message) {
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

	public static MemberLevelEnum getByValue(Integer value) {
		for (MemberLevelEnum state : values()) {
			if (state.getValue().equals(value)) {
				return state;
			}
		}
		return null;
	}
}


