package com.yuqian.itax.park.enums;

/**
 * 园区对公户提现状态枚举
 *
 * @author：yejian
 * @Date：2020/09/09 14:12
 * @version：1.0
 */
public enum ParkCorporateAccountConfigStatusEnum {
    DISABLED(0, "不可用"),
    AVAILABLE(1, "可用"),
    ;

    private Integer value;

    private String message = null;

    private ParkCorporateAccountConfigStatusEnum(Integer value, String message) {
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
    public static ParkCorporateAccountConfigStatusEnum getByValue(Integer value) {
        for (ParkCorporateAccountConfigStatusEnum statusEnum : values()) {
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

