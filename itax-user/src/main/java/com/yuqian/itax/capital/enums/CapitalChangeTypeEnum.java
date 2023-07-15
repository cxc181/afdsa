package com.yuqian.itax.capital.enums;

/**
 *  @Author: yejian
 *  @Date: 2019/12/12 20:20
 *  @Description: 用户资金变动类型枚举
 *  1-收入 2-支出 3-冻结 4-解冻
 */
public enum CapitalChangeTypeEnum {
    INCOME(1, "收入"),
    EXPENDITURE(2, "支出"),
    FROZEN(3, "冻结"),
    THAW(4, "解冻");

    private Integer value;

    private String message = null;

    private CapitalChangeTypeEnum(Integer value, String message) {
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
    public static CapitalChangeTypeEnum getByValue(Integer value) {
        for (CapitalChangeTypeEnum statusEnum : values()) {
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

