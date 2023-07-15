package com.yuqian.itax.user.enums;

/**
 * 客服状态 0-禁用 1-正常
 */
public enum CustomerStatusEnum {
    FORBIDDEN(0, "禁用"),
    NORMAL(1, "正常");


    private Integer value;
    private String message = null;



    private CustomerStatusEnum(Integer value, String message) {
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
    public static CustomerStatusEnum getByValue(Integer value) {
        for (CustomerStatusEnum levelEnum : values()) {
            if (levelEnum.getValue().equals(value)) {
                return levelEnum;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return value + "|" + message;
    }

}
