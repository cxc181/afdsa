package com.yuqian.itax.order.enums;

/**
 * 发票类型枚举 1-纸质发票 2-电子发票
 * @author：pengwei
 * @Date：2020/12/25 16:13
 * @version：1.0
 */
public enum InvoiceWayEnum {

    PAPER(1, "纸质发票"),
    ELECTRON(2, "电子发票"),
    ;

    private Integer value;

    private String message = null;

    private InvoiceWayEnum(Integer value, String message) {
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
    public static InvoiceWayEnum getByValue(Integer value) {
        for (InvoiceWayEnum statusEnum : values()) {
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

