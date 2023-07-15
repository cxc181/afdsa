
package com.yuqian.itax.order.enums;

/**
 * 续费类型枚举：1-托管费 2-对公户
 * @author：LiuMenghao
 * @Date：2021/02/07
 * @version：1.8
 */
public enum ContTypeEnum {
	TRUSTEE_FEE(1, "托管费"),
	CORPORATE_ACCOUNT(2, "对公户");

	private Integer value;
	private String message;

	private ContTypeEnum(Integer value, String message) {
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

	public static ContTypeEnum getByValue(Integer value) {
		for (ContTypeEnum state : values()) {
			if (state.getValue().equals(value)) {
				return state;
			}
		}
		return null;
	}
}


