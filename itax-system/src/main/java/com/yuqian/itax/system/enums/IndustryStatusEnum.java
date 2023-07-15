package com.yuqian.itax.system.enums;

/**
 * 状态 0-不可用 1-可用
 * @author：pengwei
 * @Date：2019/12/17 14:12
 * @version：1.0
 */
public enum IndustryStatusEnum {

    NO(0, "不可用"),
    YES(1, "可用");

    private Integer value;

    private String message = null;

    private IndustryStatusEnum(Integer value, String message) {
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
    public static IndustryStatusEnum getByValue(Integer value) {
        for (IndustryStatusEnum statusEnum : values()) {
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

