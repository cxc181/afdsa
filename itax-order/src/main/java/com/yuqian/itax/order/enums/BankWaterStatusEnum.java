/**
 * Linkea.cn Inc.
 * Copyright (c) 2010-2012 All Rights Reserved.
 */
package com.yuqian.itax.order.enums;

/**
 * 开票订单流水状态枚举：0-待上传 1-审核中 2-审核通过 3-审核不通过 4-流水前置
 * @author：yejian
 * @Date：2020/05/18 9:45
 * @version：1.0
 */
public enum BankWaterStatusEnum {
	TO_BE_UPLOAD(0, "待上传"),
	TO_BE_AUDIT(1, "审核中"),
	APPROVED(2, "审核通过"),
	NOT_APPROVED(3, "审核不通过"),
	WATER_FRONT(4, "流水前置");
	private Integer value;
	private String message;

	private BankWaterStatusEnum(Integer value, String message) {
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

	public static BankWaterStatusEnum getByValue(Integer value) {
		for (BankWaterStatusEnum state : values()) {
			if (state.getValue().equals(value)) {
				return state;
			}
		}
		return null;
	}
}


