package com.yuqian.itax.tax.enums;

/**
 *  @Date: 2019/12/20 16:45
 */
public enum VouchersStatusEnum {

    TO_BE_CONFIRMED(0, "未上传"),
    VOUCHERS_TO_BE_REFUNDED(1, "解析中"),
    VOUCHERS_TO_BE_PAID(2, "已上传"),
    VOUCHERS_REFUNDED(3, "部分已上传"),
            ;

    private Integer value;

    private String message = null;

    private VouchersStatusEnum(Integer value, String message) {
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
    public static VouchersStatusEnum getByValue(Integer value) {
        for (VouchersStatusEnum statusEnum : values()) {
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
