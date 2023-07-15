package com.yuqian.itax.park.enums;

/**
 *  @Author: Kaven
 *  @Date: 2019/12/9 14:49
 *  @Description: 税费政策状态枚举
 *  0-待上架 1-已上架 2-已下架 3-已暂停
 */
public enum TaxPolicyStatusEnum {
    STAY_SHELF(0, "待上架"),
    ON_SHELF(1, "已上架"),
    OFF_SHELF(2, "已下架"),
    PAUSED(3, "已暂停");

    private Integer value;

    private String message = null;

    private TaxPolicyStatusEnum(Integer value, String message) {
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
    public static TaxPolicyStatusEnum getByValue(Integer value) {
        for (TaxPolicyStatusEnum statusEnum : values()) {
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

