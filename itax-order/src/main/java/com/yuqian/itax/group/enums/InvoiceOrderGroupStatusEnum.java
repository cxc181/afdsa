/**
 * Linkea.cn Inc.
 * Copyright (c) 2010-2012 All Rights Reserved.
 */
package com.yuqian.itax.group.enums;

/**
 * 批量出票状态枚举
 * @author：pengwei
 * @Date：2019/12/6 11:12
 * @version：1.0
 */
public enum InvoiceOrderGroupStatusEnum {
	CREATED(0,"流水解析中"),
	IN_TICKETING(1, "出票中"),
	SIGNED(2, "已签收"),
	CANCELED(3, "已取消"),
	EXAMINE(4,"待财务审核");
	private Integer value;
	private String message;

	private InvoiceOrderGroupStatusEnum(Integer value, String message) {
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

	public static InvoiceOrderGroupStatusEnum getByValue(Integer value) {
		for (InvoiceOrderGroupStatusEnum state : values()) {
			if (state.getValue().equals(value)) {
				return state;
			}
		}
		return null;
	}
}


