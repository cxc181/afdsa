package com.yuqian.itax.order.enums;

/**
 *  @Author: Kaven
 *  @Date: 2019/12/9 14:49
 *  @Description: 发票标识枚举
 *  0-正常 1-已作废/红冲 2-作废重开
 */
public enum InvoiceMarkEnum {
    NORMAL(0,"正常"),
    CANCELLATION(1,"已作废/红冲"),
    REOPEN(2, "作废重开");

    private Integer value;

    private String message = null;

    private InvoiceMarkEnum(Integer value, String message) {
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
    public static InvoiceMarkEnum getByValue(Integer value) {
        for (InvoiceMarkEnum statusEnum : values()) {
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

