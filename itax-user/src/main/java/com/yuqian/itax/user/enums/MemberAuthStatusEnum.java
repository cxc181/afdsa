package com.yuqian.itax.user.enums;

/**
 *  @Author: Kaven
 *  @Date: 2020/2/13 11:08
 *  @Description: 会员实名认证状态枚举类 0-未认证 1-认证成功 2-认证失败
 */
public enum MemberAuthStatusEnum {
    UN_AUTH(0, "未认证"),
    AUTH_SUCCESS(1, "认证成功"),
    AUTH_FAIL(2, "认证失败");
    private Integer value;
    private String message;

    private MemberAuthStatusEnum(Integer value, String message) {
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

    public static MemberAuthStatusEnum getByValue(Integer value) {
        for (MemberAuthStatusEnum state : values()) {
            if (state.getValue().equals(value)) {
                return state;
            }
        }
        return null;
    }
}