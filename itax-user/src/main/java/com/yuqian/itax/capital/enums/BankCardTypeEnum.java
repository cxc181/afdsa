package com.yuqian.itax.capital.enums;

/**
 *  @Author: Kaven
 *  @Date: 2019/12/21 15:15
 *  @Description: 银行卡类型枚举
 */
public enum BankCardTypeEnum {

    DEBIT_CARD(1, "储蓄卡"),
    CREDIT_CARD(2, "信用卡");

    private Integer value;

    private String message = null;

    private BankCardTypeEnum(Integer value, String message) {
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
    public static BankCardTypeEnum getByValue(Integer value) {
        for (BankCardTypeEnum statusEnum : values()) {
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

