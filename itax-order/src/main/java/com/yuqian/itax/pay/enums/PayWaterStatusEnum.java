package com.yuqian.itax.pay.enums;

/**
 * @Author Kaven
 * @Description 支付流水状态枚举
 * 支付状态 0-待支付 1-支付中 2 -支付成功 3-支付失败 4-待财务审核 5-财务审核失败
 * @Date 16:27 2019/8/13
 * @Param
 * @return
 **/
public enum PayWaterStatusEnum {

    PAY_INIT(0, "待支付"),
    PAYING(1, "支付中"),
    PAY_SUCCESS(2, "支付成功"),
    PAY_FAILURE(3, "支付失败"),
    WAIT_FOR_AUDIT(4, "待财务审核"),
    AUDIT_FAIL(5, "财务审核失败"),
    WAIT_FINANCIAL_PAY(6, "待财务打款"),
    ;

    private Integer value;

    private String message = null;

    private PayWaterStatusEnum(Integer value, String message) {
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
    public static PayWaterStatusEnum getByValue(Integer value) {
        for (PayWaterStatusEnum statusEnum : values()) {
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

