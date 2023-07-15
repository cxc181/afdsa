package com.yuqian.itax.coupons.entity.enums;

/**
 * 兑换码状态枚举
 */
public enum CouponExchangeCodeStatusEnum {
    INEFFICIENT(0, "未生效"),
    EFFICIENT(1, "已生效"),
    STALE(2, "已过期"),
    OBSOLETE(3, "已作废"),
    PAUSED(4, "已暂停"),
    ;

    private Integer value;
    private String message;

    private CouponExchangeCodeStatusEnum(Integer value, String message) {
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
}
