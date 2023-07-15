package com.yuqian.itax.product.enums;

/**
 * 产品管理状态 0-待上架 1-已上架 2-已下架 3-已暂停
 * @author：pengwei
 * @Date：2019/12/16 20:12
 * @version：1.0
 */
public enum ProductStatusEnum {

    STAY_SHELF(0, "待上架"),
    ON_SHELF(1, "已上架"),
    OFF_SHELF(2, "已下架"),
    PAUSED(3, "已暂停");

    private Integer value;

    private String message = null;

    private ProductStatusEnum(Integer value, String message) {
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
    public static ProductStatusEnum getByValue(Integer value) {
        for (ProductStatusEnum statusEnum : values()) {
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

