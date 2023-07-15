/**
 * Linkea.cn Inc.
 * Copyright (c) 2010-2012 All Rights Reserved.
 */
package com.yuqian.itax.profits.enums;

/**
 * 分润类型枚举
 *
 * @author：Kaven
 * @Date：2020/5/18 11:18
 * @version：1.0
 */
public enum ProfitsTypeEnum {
	MEMBER_UPGRADE_FEE(1, "会员升级费"),
	CUSTODIAN_FEE(2, "托管费"),
	INVOICE_SERVICE_FEE(3, "开票服务费"),
	CANCELLATION_FEE(4, "注销服务费"),
	MEMBER_UPGRADE_RETURN_FEE(5, "会费返还"),
	CUSTODY_FEE_RENEWAL(6, "托管费续费"),
	;
	private Integer value;
	private String message;

	private ProfitsTypeEnum(Integer value, String message) {
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

	public static ProfitsTypeEnum getByValue(Integer value) {
		for (ProfitsTypeEnum state : values()) {
			if (state.getValue().equals(value)) {
				return state;
			}
		}
		return null;
	}
}


