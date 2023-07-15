package com.yuqian.itax.order.enums;

/**
 *  @Author: pengwei
 *  @Date: 2019/12/30 14:49
 *  @Description: 开票订单状态枚举
 *  状态 0-待提交、1-人工出票、2-待补票、3-出票中断、4-待确认、5-推送失败、6-待确认、7-已完成 8-出票失败
 */
public enum InvoiceRecordStatusEnum {
    TO_SUBMIT(0,"待提交"),
    ARTIFICIAL_TICKET(1, "人工出票"),
    FOR_TICKET(2, "待补票"),
    NTERRUPTION_OF_THE_DRAWER(3, "出票中断"),
    THE_DRAWER_FAILURE(4, "出票失败"),
    FAILED_TO_PUSH(5, "推送失败"),
    TO_BE_CONFIRMED(6, "待确认"),
    COMPLETED(7, "已完成"),
    CANCELED(8, "已取消"),
    INVOICING(9, "出票中"),
    ;

    private Integer value;

    private String message = null;

    private InvoiceRecordStatusEnum(Integer value, String message) {
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
    public static InvoiceRecordStatusEnum getByValue(Integer value) {
        for (InvoiceRecordStatusEnum statusEnum : values()) {
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

