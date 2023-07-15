/**
 * Linkea.cn Inc.
 * Copyright (c) 2010-2012 All Rights Reserved.
 */
package com.yuqian.itax.order.enums;

/**
 * 工商注册枚举
 * @author：pengwei
 * @Date：2019/12/6 11:12
 * @version：1.0
 * 0-待电子签字,1-待视 频认证,2-审核中,3-待付款,4-待领证,5-已完成,6-已取消,7-审核失败,8-核名驳回,9-待身份验证，
 * 10-待设立登记、11-待提交签名、12-签名待确认 13-待创建
 */
public enum RegOrderStatusEnum {
	TO_BE_SIGN(0, "待电子签字"),
	TO_BE_VIDEO(1, "待视频认证"),
	TO_BE_SURE(2, "审核中"),
	TO_BE_PAY(3, "待付款"),
	TO_BE_CERTIFICATION(4, "待领证"),
	COMPLETED(5, "已完成"),
	CANCELLED(6, "已取消"),
	FAILED(7, "审核失败"),
	REJECTED(8, "核名驳回"),
	TO_BE_VALIDATE(9, "待身份验证"),
	REGISTRATION_UNDER_WAY(10, "待设立登记"),
	SIGNATURE_TO_BE_SUBMITTED(11, "待提交签名"),
	SIGNATURE_CONFIRMATION(12, "已提交签名"),
	TO_CREATE(13, "待创建"),
	;
	private Integer value;
	private String message;

	private RegOrderStatusEnum(Integer value, String message) {
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

	public static RegOrderStatusEnum getByValue(Integer value) {
		for (RegOrderStatusEnum state : values()) {
			if (state.getValue().equals(value)) {
				return state;
			}
		}
		return null;
	}
}


