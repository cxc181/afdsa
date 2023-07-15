package com.yuqian.itax.park.enums;

/**
 * 计税方式（1：预缴征收率，2：核定应税所得率）
 * @author：pengwei
 * @Date：2020/11/11 10:05
 * @version：1.0
 */
public enum LevyWayEnum {

    LEVY_RATE(1, "预缴征收率"),
    TAXABLE_INCOME_RATE(2, "核定应税所得率");

    private Integer value;

    private String message;

    private LevyWayEnum(Integer value, String message) {
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
    public static LevyWayEnum getByValue(Integer value) {
        for (LevyWayEnum statusEnum : values()) {
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

