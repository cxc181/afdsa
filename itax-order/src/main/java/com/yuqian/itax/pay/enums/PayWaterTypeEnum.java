package com.yuqian.itax.pay.enums;

/**
 *  @Author: Kaven
 *  @Date: 2019/12/12 10:45
 *  @Description: 流水类型枚举类
 *  流水类型 1-充值，2-提现 ，3-余额支付，4-第三方支付，5-退款，6-对公户提现，7-企业退税
 */
public enum PayWaterTypeEnum {

    RECHARGE(1, "充值"),
    WITHDRAW(2, "提现"),
    BALANCE(3, "余额支付"),
    THIRD(4, "第三方支付"),
    REFUND(5, "退款"),
    CORP_WITHDRAW(6, "对公户提现"),
    TAX_REFUND(7, "企业退税"),
    OFFLINE(8, "线下支付"),
    ;

    private Integer value;

    private String message = null;

    private PayWaterTypeEnum(Integer value, String message) {
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
    public static PayWaterTypeEnum getByValue(Integer value) {
        for (PayWaterTypeEnum statusEnum : values()) {
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

