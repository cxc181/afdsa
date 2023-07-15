package com.yuqian.itax.pay.enums;

/**
 *  @Author: Kaven
 *  @Date: 2019/12/12 10:45
 *  @Description: 支付方式枚举类
 *  支付方式  1-微信 2-余额 3-支付宝 4-快捷支付
 */
public enum PayWayEnum {

    WECHATPAY(1, "微信"),
    BALANCEPAY(2, "余额"),
    ALIPAY(3, "支付宝"),
    QUICKPAY(4, "快捷支付"),
    BYTEDANCE(5, "字节跳动"),
    OFFLINE(6,"线下转账");

    private Integer value;

    private String message = null;

    private PayWayEnum(Integer value, String message) {
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
    public static PayWayEnum getByValue(Integer value) {
        for (PayWayEnum statusEnum : values()) {
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

