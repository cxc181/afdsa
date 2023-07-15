package com.yuqian.itax.pay.enums;

/**
 * @Author Kaven
 * @Description 支付流水退款状态枚举
 * 退款状态 1-退款中 2-退款成功 3-退款失败
 * @Param
 * @return
 **/
public enum RefundWaterStatusEnum {

    REFUNDING(1, "退款中"),
    REFUND_SUCCESS(2, "退款成功"),
    REFUND_FAILURE(3, "退款失败"),
    ;

    private Integer value;

    private String message = null;

    private RefundWaterStatusEnum(Integer value, String message) {
        this.value = value;
        this.message = message;
    }

    public Integer getValue() {
        return value;
    }

    public String getMessage() {
        return message;
    }

    /**
     * 通过枚举<code>value</code>获得枚举
     * 
     * @param value
     * @return
     */
    public static RefundWaterStatusEnum getByValue(Integer value) {
        for (RefundWaterStatusEnum statusEnum : values()) {
            if (statusEnum.getValue().equals(value)) {
                return statusEnum;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return value + "|" + message;
    }

}

