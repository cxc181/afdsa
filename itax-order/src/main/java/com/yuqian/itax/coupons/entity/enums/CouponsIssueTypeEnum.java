package com.yuqian.itax.coupons.entity.enums;

public enum CouponsIssueTypeEnum {
    BATCH(0, "批量发放"),
    EXCHANGE(1, "兑换码"),
    ;

    private Integer value;
    private String message;

    private CouponsIssueTypeEnum(Integer value, String message) {
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
