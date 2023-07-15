package com.yuqian.itax.pay.enums;

/**
 *  @Author: Kaven
 *  @Date: 2019/12/20 16:45
 *  @Description: 支付通道枚举类
 *  支付通道 1-微信 2-余额 3-支付宝 4-易宝支付 5-建行 6-北京代付 7-纳呗
 */
public enum PayChannelEnum {

    WECHATPAY(1, "微信"),
    BALANCEPAY(2, "余额"),
    ALIPAY(3, "支付宝"),
    YOPPAY(4, "易宝支付"),
    CCBPAY(5, "建行"),
    PROXYPAY(6, "北京代付"),
    NABEIPAY(7, "纳呗"),
    BYTEDANCE(8, "字节跳动"),
    OFFLINE(9, "线下"),
    YISHUIPAY(10, "易税");

    private Integer value;

    private String message = null;

    private PayChannelEnum(Integer value, String message) {
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
    public static PayChannelEnum getByValue(Integer value) {
        for (PayChannelEnum statusEnum : values()) {
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

