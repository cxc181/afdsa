package com.yuqian.itax.order.enums;

/**
 * 驳回项枚举
 * @author：liumenghao
 * @Date：2021/4/8
 */
public enum RejectedItemEnum {

    SHOP(1, "字号"),
    ID_CARD(2, "身份证"),
    VIDEO_ADDR(3,"视频"),
    ;

    private Integer value;

    private String message = null;

    private RejectedItemEnum(Integer value, String message) {
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
    public static RejectedItemEnum getByValue(Integer value) {
        for (RejectedItemEnum statusEnum : values()) {
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

