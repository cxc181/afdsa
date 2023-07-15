package com.yuqian.itax.order.enums;

/**
 * @ClassName: InvoiceTypeEnum
 * @Description: 开票类型枚举
 * 发票类型 1-增值税普通发票 2-增值税专用发票
 * @Author: yejian
 * @Date: Created in 2019/12/9
 * @Version: 1.0
 * @Modified By:
 */
public enum InvoiceTypeEnum {

    UPGRADE(1, "增值税普通发票"),
    REGISTER(2, "增值税专用发票");

    private Integer value;

    private String message = null;

    private InvoiceTypeEnum(Integer value, String message) {
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
    public static InvoiceTypeEnum getByValue(Integer value) {
        for (InvoiceTypeEnum statusEnum : values()) {
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

