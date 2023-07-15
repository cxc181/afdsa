package com.yuqian.itax.order.enums;

/**
 * @Description 消费开票订单状态枚举类 0-待出票 1-出票中 2-已出票 3-出票失败
 * @Author  Kaven
 * @Date   2020/9/27 14:59
*/
public enum ConsumtionInvOrderStatusEnum {
    TO_TICKET(0, "待出票"),
    TICKETING(1, "出票中"),
    TICKETED(2, "已出票"),
    TICKET_FAIL(3, "出票失败"),
    ;

    private Integer value;

    private String message = null;

    private ConsumtionInvOrderStatusEnum(Integer value, String message) {
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
    public static ConsumtionInvOrderStatusEnum getByValue(Integer value) {
        for (ConsumtionInvOrderStatusEnum statusEnum : values()) {
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

