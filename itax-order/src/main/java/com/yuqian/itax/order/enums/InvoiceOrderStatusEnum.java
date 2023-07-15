package com.yuqian.itax.order.enums;

/**
 *  @Author: Kaven
 *  @Date: 2019/12/9 14:49
 *  @Description: 开票订单状态枚举
 *  0-待创建 1-待付款 2-待审核 3-出票中 4-待发货 5-出库中 6-待收货 7-已签收 8-已取消 9-待出款
 */
public enum InvoiceOrderStatusEnum {
    CREATED(0,"待创建"),
    UNPAID(1, "待付款"),
    UNCHECKED(2, "待审核"),
    IN_TICKETING(3, "出票中"),
    TO_BE_SHIPPED(4, "待发货"),
    OUT_OF_STOCK(5, "出库中"),
    TO_BE_RECEIVED(6, "待收货"),
    SIGNED(7, "已签收"),
    CANCELED(8, "已取消"),
    WAIT_FOR_PAYMENT(9, "待出款"),
    AUDIT_FAILED(10, "审核未通过"),
    TO_PAYMENT_REVIEW(11, "待财务审核");

    private Integer value;

    private String message = null;

    private InvoiceOrderStatusEnum(Integer value, String message) {
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
    public static InvoiceOrderStatusEnum getByValue(Integer value) {
        for (InvoiceOrderStatusEnum statusEnum : values()) {
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

