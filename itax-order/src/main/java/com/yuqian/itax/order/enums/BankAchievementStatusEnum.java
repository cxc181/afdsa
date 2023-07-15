/**
 * Linkea.cn Inc.
 * Copyright (c) 2010-2012 All Rights Reserved.
 */
package com.yuqian.itax.order.enums;

/**
 * 开票订单成果状态 0-无需上传 1-成果前置 2-待上传 3-审核中 4-审核不通过 5-审核通过
 * @author：yejian
 * @Date：2020/05/18 9:45
 * @version：1.0
 */
public enum BankAchievementStatusEnum {
	NOT_NEED_UPLOAD(0, "无需上传"),
	ACHIEVEMENT(1, "成果前置"),
	TO_BE_UPLOAD(2, "待上传"),
	TO_BE_AUDIT(3, "审核中"),
	NOT_APPROVED(4, "审核不通过"),
	APPROVED(5, "审核通过")
	;
	private Integer value;
	private String message;

	private BankAchievementStatusEnum(Integer value, String message) {
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

	public static BankAchievementStatusEnum getByValue(Integer value) {
		for (BankAchievementStatusEnum state : values()) {
			if (state.getValue().equals(value)) {
				return state;
			}
		}
		return null;
	}
}


