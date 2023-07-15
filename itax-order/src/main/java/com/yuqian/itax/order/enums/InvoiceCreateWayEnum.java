package com.yuqian.itax.order.enums;

/**
 * @Description 开票方式枚举 1-自助开票 2-集团批量开票 3-佣金开票
 * @Author  robb
 * @Date   2020/6/23 16:06
*/
public enum InvoiceCreateWayEnum {

	ONESELF(1, "自助开票"),
	GROUP(2, "集团批量开票"),
	COMMISSION(3, "佣金开票"),
	THIRDPRATY(5, "接入方开票"),
	;
	private Integer value;
	private String message;

	private InvoiceCreateWayEnum(Integer value, String message) {
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

	public static InvoiceCreateWayEnum getByValue(Integer value) {
		for (InvoiceCreateWayEnum state : values()) {
			if (state.getValue().equals(value)) {
				return state;
			}
		}
		return null;
	}
}


