package com.yuqian.itax.order.enums;

public enum OrderStatusEnum {

    UNPAID(0, "待付款"),
    WAIT_APPOINTMENT(1,"等待预约"),
    COMPLETED(2,"已完成"),
    CANCELED(3, "已取消")
    ;

    private Integer value;

    private String message = null;

    OrderStatusEnum(Integer value, String message) {
        this.value = value;
        this.message = message;
    }

    public Integer getValue() {
        return value;
    }

    public String getMessage() {
        return message;
    }


    public static OrderStatusEnum getByValue(Integer value) {
        for (OrderStatusEnum statusEnum : values()) {
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
