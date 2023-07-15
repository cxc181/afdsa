package com.yuqian.itax.user.enums;

/**
 * 企业资源申请状态枚举
 * @author：yejian
 * @Date：2019/12/10 16:07
 * @version：1.0
 */
public enum CompanyResoucesApplyRecordStatusEnum {

    OBLIGATION(0, "待付款"),
    TO_BE_SHIPPED(1, "待发货"),
    OUT_OF_STOCK(2, "出库中"),
    TO_SIGN(3, "待签收"),
    SIGNED(4, "已签收"),
    CANCELED(5, "已取消");
    private Integer value;
    private String message;

    private CompanyResoucesApplyRecordStatusEnum(Integer value, String message) {
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

    public static CompanyResoucesApplyRecordStatusEnum getByValue(Integer value) {
        for (CompanyResoucesApplyRecordStatusEnum state : values()) {
            if (state.getValue().equals(value)) {
                return state;
            }
        }
        return null;
    }
}
