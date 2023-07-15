package com.yuqian.itax.park.enums;

/**
 * 所得税征收方式 1-查账征收 2-核定征收
 *
 */
public enum IncomeLevyTypeEnum {

    AUDIT_COLLECTION(1, "查账征收"),
    VERIFICATION_COLLECTION(2, "核定征收");

    private Integer value;

    private String message;

    private IncomeLevyTypeEnum(Integer value, String message) {
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
    public static IncomeLevyTypeEnum getByValue(Integer value) {
        for (IncomeLevyTypeEnum statusEnum : values()) {
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

