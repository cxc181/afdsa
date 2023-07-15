package com.yuqian.itax.agent.enums;

/**
 * 分润结算周期
 * @author：pengwei
 * @Date：2019/12/17 14:12
 * @version：1.0
 */
public enum OemSettlementTypeEnum {

    BY_WEEK(1, "按周结算"),
    BY_MONTH(2, "按月结算");

    private Integer value;

    private String message = null;

    private OemSettlementTypeEnum(Integer value, String message) {
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
    public static OemSettlementTypeEnum getByValue(Integer value) {
        for (OemSettlementTypeEnum statusEnum : values()) {
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

