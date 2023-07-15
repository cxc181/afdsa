/**
 * Linkea.cn Inc.
 * Copyright (c) 2010-2012 All Rights Reserved.
 */
package com.yuqian.itax.user.enums;

/**
 * @author：pengwei
 * @Date：2020/9/8 9:13
 * @version：1.0
 * 是否付费升级 0-否 1-是
 */
public enum MemberPayUpgradeEnum {
	NO(0, "否"),
	YES(1, "是"),
	;
	private Integer value;
	private String message;

	private MemberPayUpgradeEnum(Integer value, String message) {
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

	public static MemberPayUpgradeEnum getByValue(Integer value) {
		for (MemberPayUpgradeEnum state : values()) {
			if (state.getValue().equals(value)) {
				return state;
			}
		}
		return null;
	}
}


