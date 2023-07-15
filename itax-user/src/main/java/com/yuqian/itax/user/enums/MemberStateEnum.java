/**
 * Linkea.cn Inc.
 * Copyright (c) 2010-2012 All Rights Reserved.
 */
package com.yuqian.itax.user.enums;

/**
 * 
 * @author elliott
 * @version $Id: StateEnum.java, v 0.1 2012-3-27 下午5:41:31 elliott Exp $
 */
public enum MemberStateEnum {
	STATE_ACTIVE(1, "可用"), STATE_UNACTIVE(0, "禁用"), STATE_OFF(2, "注销");
	private Integer value;
	private String message;

	private MemberStateEnum(Integer value, String message) {
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

	public static MemberStateEnum getByValue(Integer value) {
		for (MemberStateEnum state : values()) {
			if (state.getValue().equals(value)) {
				return state;
			}
		}
		return null;
	}
}


