package com.yuqian.itax.park.enums;

/**
 * 征收方式枚举（1：核定征收率，2：核定应税所得率）
 * @author：pengwei
 * @Date：2020/11/11 10:05
 * @version：1.0
 */
public enum TaxTypeEnum {
    IIT(1, "所得税"),
    VAT(2, "增值税"),
    SURCHARGE(3, "附加税"),
    STAMP_DUTY(4, "印花税"),
    FOUNDATION_FOR_WATER_WORKS(5, "水利建设基金");

    private Integer value;

    private String message;

    private TaxTypeEnum(Integer value, String message) {
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
    public static TaxTypeEnum getByValue(Integer value) {
        for (TaxTypeEnum statusEnum : values()) {
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

