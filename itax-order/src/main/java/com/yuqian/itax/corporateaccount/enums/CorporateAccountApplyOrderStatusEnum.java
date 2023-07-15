package com.yuqian.itax.corporateaccount.enums;

/**
 * 对公户申请订单状态枚举
 *
 * @author：yejian
 * @Date：2020/09/09 11:07
 * @version：1.0
 */
public enum CorporateAccountApplyOrderStatusEnum {

    TO_BE_PAY(0, "待支付"),
    TO_BE_SUBSCRIBE(1, "等待预约"),
    COMPLETED(2, "已完成"),
    CANCELED(3, "已取消");
    private Integer value;
    private String message;

    private CorporateAccountApplyOrderStatusEnum(Integer value, String message) {
        this.value = value;
        this.message = message;
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public static CorporateAccountApplyOrderStatusEnum getByValue(Integer value) {
        for (CorporateAccountApplyOrderStatusEnum state : values()) {
            if (state.getValue().equals(value)) {
                return state;
            }
        }
        return null;
    }
}
