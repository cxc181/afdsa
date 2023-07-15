package com.yuqian.itax.order.enums;

/**
 *  @Author: Kaven
 *  @Date: 2019/12/9 14:49
 *  @Description: 订单类型枚举
 *  订单类型 1-充值 2-代理充值 3-提现 4-代理提现 5 - 工商注册 6-开票 7-用户升级 8-工商注销 9-证件申请 10-对公户申请 11-对公户提现 12-消费开票 13-补税 14-退税
 */
public enum OrderTypeEnum {

    ENCHARGE(1, "充值"),
    SUBSTITUTE_CHARGE(2, "代充值"),
    WITHDRAW(3, "提现"),
    SUBSTITUTE_WITHDRAW(4, "代提现"),
    REGISTER(5, "工商注册"),
    INVOICE(6, "开票"),
    UPGRADE(7, "会员升级"),
    CANCELLATION(8, "公司注销"),
    COMPRESAPPLY(9, "证件领用"),
    CORPORATE_APPLY(10, "对公户申请"),
    CORPORATE_WITHDRAW(11, "对公户提现"),
    CONSUMPTION_INVOICE(12, "消费开票"),
    RECOVERABLE_TAX(13, "补税"),
    SUPPLEMENT_TAX(14, "退税"),
    CUSTODY_FEE_RENEWAL(15, "托管费续费"),
    CORPORATE_ACCOUNT_RENEW(16, "对公户续费"),
    ;

    private Integer value;

    private String message = null;

    private OrderTypeEnum(Integer value, String message) {
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
    public static OrderTypeEnum getByValue(Integer value) {
        for (OrderTypeEnum statusEnum : values()) {
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

