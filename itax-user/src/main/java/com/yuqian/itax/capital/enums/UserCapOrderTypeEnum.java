package com.yuqian.itax.capital.enums;

/**
 *  @Author: Kaven
 *  @Date: 2019/12/13 10:32
 *  @Description: 订单类型枚举
 *  订单类型 1-充值 2-代理充值 3-提现 4-代理提现 5 - 工商开户 6-开票 7-会费
 */
public enum UserCapOrderTypeEnum {
    RECHARGE(1, "充值"),
    PROXY_RECHARGE(2, "代理充值"),
    WITHDRAW(3, "提现"),
    PROXY_WITHDRAW(4, "代理提现"),
    REGISTER(5, "工商开户"),
    INVOICE(6, "开票"),
    MEMBER_FEES(7, "会费"),
    CANCELLATION(8, "工商注销"),
    COMPANY_APPLY_RECORD(9, "企业资源申请");

    private Integer value;

    private String message = null;

    private UserCapOrderTypeEnum(Integer value, String message) {
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
    public static UserCapOrderTypeEnum getByValue(Integer value) {
        for (UserCapOrderTypeEnum statusEnum : values()) {
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

