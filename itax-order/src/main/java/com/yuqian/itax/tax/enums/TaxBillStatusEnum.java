package com.yuqian.itax.tax.enums;

/**
 *  @Date: 2019/12/20 16:45
 *  支税单状态 0-待确认 1-待退税 2-待补税 3-正常 4-已退税 5-已补税 6-待核对 7-待填报成本 8-待申报 9-已作废
 */
public enum TaxBillStatusEnum {

    TO_BE_CONFIRMED(0, "待确认"),
    TAX_TO_BE_REFUNDED(1, "待退税"),
    TAX_TO_BE_PAID(2, "待补税"),
    NORMAL(3, "正常"),
    TAX_REFUNDED(4, "已退税"),
    TAX_PAID(5, "已补税"),
    TO_BE_CHECK(6, "待核对"),
    TO_BE_WRITE_COST(7, "待填报成本"),
    TO_BE_DECLARE(8, "待申报"),
    CANCELLED(9, "已作废"),
    TO_FINANCIAL_AUDIT(10, "待财务审核"),
    AUDIT_FAILED(11, "审核不通过"),
    ;

    private Integer value;

    private String message = null;

    private TaxBillStatusEnum(Integer value, String message) {
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
    public static TaxBillStatusEnum getByValue(Integer value) {
        for (TaxBillStatusEnum statusEnum : values()) {
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

