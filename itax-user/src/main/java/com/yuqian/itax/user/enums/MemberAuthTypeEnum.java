package com.yuqian.itax.user.enums;

/**
 *
 * @Description: 会员身份类型枚举类 0-未知 1-个人 2-企业
 */
public enum MemberAuthTypeEnum {
    UNKNOWN(0, "未知"),
    PERSON(1, "个人"),
    COMPANY(2, "企业");
    private Integer value;
    private String message;

    private MemberAuthTypeEnum(Integer value, String message) {
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

    public static MemberAuthTypeEnum getByValue(Integer value) {
        for (MemberAuthTypeEnum state : values()) {
            if (state.getValue().equals(value)) {
                return state;
            }
        }
        return null;
    }
}