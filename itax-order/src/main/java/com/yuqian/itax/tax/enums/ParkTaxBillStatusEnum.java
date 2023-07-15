package com.yuqian.itax.tax.enums;

/**
 *  @Date: 2019/12/20 16:45
 */
public enum ParkTaxBillStatusEnum {

    TO_BE_CONFIRMED(0, "待确认"),
    TAX_TO_BE_REFUNDED(1, "解析中"),
    TAX_TO_BE_PAID(2, "待上传"),
    TAX_REFUNDED(3, "已确认"),
    TO_BE_UPLOAD_VOUCHERS(4, "待上传凭证"),
    PUSHING(5, "推送中"),
    PUSH(6, "已推送"),
    TO_BE_CHECK(7, "待核对"),
    PENDING(8, "待处理"),
    PROCESSED(9, "已处理"),
    PENDIDNG_FINANCIAL_REVIEW(10,"待财务审核"),
    FAILED_TO_PASS_THE_REVIEW(11,"审核不通过"),
    ;

    private Integer value;

    private String message = null;

    private ParkTaxBillStatusEnum(Integer value, String message) {
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
    public static ParkTaxBillStatusEnum getByValue(Integer value) {
        for (ParkTaxBillStatusEnum statusEnum : values()) {
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
