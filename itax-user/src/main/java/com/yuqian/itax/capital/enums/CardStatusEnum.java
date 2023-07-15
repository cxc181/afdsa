package com.yuqian.itax.capital.enums;

/**
 *  @Author: Kaven
 *  @Date: 2019/12/9 14:49
 *  @Description: 银行卡状态枚举
 *  银行卡状态  1-解绑 2-绑定 3-禁用
 */
public enum CardStatusEnum {

    UNBIND(1, "解绑"),
    BIND(2, "绑定"),
    FORBIDDEN(3, "禁用");

    private Integer value;

    private String message = null;

    private CardStatusEnum(Integer value, String message) {
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
    public static CardStatusEnum getByValue(Integer value) {
        for (CardStatusEnum statusEnum : values()) {
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

