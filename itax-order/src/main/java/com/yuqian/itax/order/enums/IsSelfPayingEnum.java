package com.yuqian.itax.order.enums;

/**
 *  @Author: Kaven
 *  @Date: 2019/12/9 14:49
 *  @Description: 是否自费枚举
 *  是否自费 1-自费 2-承担方
 */
public enum IsSelfPayingEnum {

    SELF_PLAYING(1, "自费"),
    BEARER(2, "承担方"),
    ;

    private Integer value;

    private String message = null;

    private IsSelfPayingEnum(Integer value, String message) {
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
    public static IsSelfPayingEnum getByValue(Integer value) {
        for (IsSelfPayingEnum statusEnum : values()) {
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

