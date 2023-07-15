package com.yuqian.itax.order.enums;

/**
 *  @Author: yejian
 *  @Date: 2020/03/25 14:49
 *  @Description: 证件领用订单状态枚举
 *  0-待发货  1-出库中 2-待签收 3-已签收 4-已取消
 */
public enum CompResApplyRecordOrderStatusEnum {
    TO_BE_PAY(0, "待付款"),
    TO_BE_SHIPPED(1, "待发货"),
    OUT_OF_STOCK(2, "出库中"),
    TO_BE_RECEIVED(3, "待签收"),
    SIGNED(4, "已签收"),
    CANCELED(5, "已取消");

    private Integer value;

    private String message = null;

    private CompResApplyRecordOrderStatusEnum(Integer value, String message) {
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
    public static CompResApplyRecordOrderStatusEnum getByValue(Integer value) {
        for (CompResApplyRecordOrderStatusEnum statusEnum : values()) {
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

