package com.yuqian.itax.user.enums;

/**
 *  @Author: Kaven
 *  @Date: 2019/12/14 14:39
 *  @Description: 支付方式枚举类 1-微信支付 2-其它 3-支付宝支付
 */
public enum PayTypeEnum {

    WECHATPAY(1, "微信支付"),
    OTHER(2, "其它"),
    ALIPAY(3, "支付宝支付"),
    BYTEDANCEPAY(4,"字节跳动");

    private Integer value;

    private String message = null;

    private PayTypeEnum(Integer value, String message) {
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
    public static PayTypeEnum getByValue(Integer value) {
        for (PayTypeEnum statusEnum : values()) {
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

