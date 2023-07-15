package com.yuqian.itax.agent.enums;

/**
 * 操作小程序来源 1-微信小程序 2-支付宝小程序
 * @author：pengwei
 * @Date：2021/1/18 16:42
 * @version：1.0
 */
public enum SourceTypeEnum {

    WHCHAT("1", "微信"),
    ALIPAY("2", "支付宝"),
    ACCESS_PARTY("3", "接入方"),
    BYTEDANCE("4", "字节跳动");
    ;

    private String value;

    private String message = null;

    private SourceTypeEnum(String value, String message) {
        this.value = value;
        this.message = message;
    }

    public String getValue() {
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
    public static SourceTypeEnum getByValue(String value) {
        for (SourceTypeEnum statusEnum : values()) {
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

