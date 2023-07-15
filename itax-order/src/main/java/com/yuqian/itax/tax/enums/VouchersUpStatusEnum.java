package com.yuqian.itax.tax.enums;

/**
 *  @Date: 2019/12/20 16:45
 */
public enum VouchersUpStatusEnum {

    VOUCHERS_TO_BE_REFUNDED(1, "未上传"),
    VOUCHERS_TO_BE_PAID(2, "已上传"),
    VOUCHERS_REFUNDED(3, "无需上传"),
            ;

    private Integer value;

    private String message = null;

    private VouchersUpStatusEnum(Integer value, String message) {
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
    public static VouchersUpStatusEnum getByValue(Integer value) {
        for (VouchersUpStatusEnum statusEnum : values()) {
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
