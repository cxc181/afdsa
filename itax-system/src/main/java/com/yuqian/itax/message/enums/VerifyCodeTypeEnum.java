package com.yuqian.itax.message.enums;


import org.apache.commons.lang3.StringUtils;

/**
 *  @Author: Kaven
 *  @Date: 2019/12/6 14:16
 *  @Description: 验证码类型的枚举
 */
public enum VerifyCodeTypeEnum {

    /*注册*/
    REGISTER("1", "注册（验证码）"),
    /* 开户*/
    OEPN_ACCOUNT("2", "开户"),
    STATISTICS_MEMBERGENERALIZE("4", "日统计"),
    MEMBER_AUTOUPDATE("3", "会员自动升级"),
    /* 通知*/
    NOTICE("5", "通知"),
    /* 用户绑卡*/
    BIND_CARD("6", "用户绑卡"),
    /* 用户解绑*/
    UNBIND_CARD("7", "用户解绑"),
    /* 用户提现*/
    WITHDRAW("8", "用户提现"),
    /* 用户开票订单支付*/
    BALANCE_PAY("9", "用户开票订单余额支付/补税"),
    /* 取消开户*/
    REGISTER_CANCELLED("10", "取消开户"),
    /* 开户成功*/
    REGISTER_SUCCESS("11", "开户成功"),
    /* 开票订单审核未通过*/
    INVOICE_FAIL("12", "开票订单审核未通过"),
    /* 开票订单审核通过*/
    INVOICE_SUCCESS("13", "开票订单审核通过"),
    /* 开票订单已发货*/
    INVOICE_SEND("14", "开票订单已发货"),
    /*账号锁定*/
    LOCK_ACCOUNT("15", "账号锁定"),
    /*忘记密码*/
    CHANGE_PASSWORD("16", "重置密码"),
    /* 开票订单已签收*/
    INVOICE_SIGN("17", "开票订单已签收"),
    /* 企业注销成功发送用户短信*/
    COMPANY_CANCEL_USER("18", "企业注销成功发送用户短信"),
    /* 企业注销成功发送经营者用户*/
    COMPANY_CANCEL_OPERATOR("19", "企业注销成功发送经营者用户"),
    /* 开户成功发送经营者用户*/
    REGISTER_SUCCESS_OPERATOR("20", "开户成功发送经营者用户"),

    COMPANY_APPLY_RECORD_SEND("23", "企业资源申请订单已发货"),

    COMPANY_APPLY_RECORD_RETURN("24", "企业资源申请订单归还"),

    INVOICE_UPLOAD_BANK_WATER("25", "开票补传资料"),

    INVOICE_WATER_FAIL("26", "开票流水审核工单不通过"),

    REGISTER_AUDIT_REJECTED("27", "企业注册订单核名驳回"),

    COMPANY_CORPORATE_ACCOUNT_SUCCESS("28", "对公户开户成功"),

    COMPANY_CORPORATE_ACCOUNT_WITHDRAW("29", "对公户提现"),

    INVOICE_APPLY_FAIL("30", "电子发票开票失败"),

    TAX_BILL("31", "税单确认后短信"),

    TAX_BILL_SUPPLEMENT("32", "税单超过15天未补交短信"),

    /* 电票开票订单已完成*/
    ELECTRONIC_INVOICE_SIGN("33", "电票开票订单已完成"),

    CUSTODIAN_WILL_EXPIRE("34", "托管费快过期短信"),

    CUSTODIAN_IS_OVERDUE("35", "托管费已过期短信"),

    LOGIN("36", "运营平台登录"),

    REGISTER_ORDER_CONFIRM_REGISTER("37", "工商注册确认登记"),

    REGISTER_ORDER_USER_NOT_SIGN("38", "工商注册用户未提交签名"),

    ACHIEVEMENT_WATER_FAIL("39", "开票成果审核工单不通过"),

    ANNUAL_FEE_WILL_EXPIRE("40", "对公户年费即将过期短信"),

    ANNUAL_FEE_IS_OVERDUE("41", "对公户年费已过期短信"),

    CANCELLATION("42","订单重开短信"),

    CONFIRM_COST_REMIND("43", "确认成本即将超时短信"),

    CONFIRM_OVERTIME_REMIND("44", "确认成本已超时短信"),

    COMPLETE_TAX_RETURN("45","完成报税通知"),

    TAX_BILL_BY_ACCOUNtS("46","税单确认后短信"),
    ;


    /**
     * 枚举值
     */
    private final String value;

    /**
     * 枚举值描述
     */
    private final String message;

    /**
     * 私有构造方法
     *
     * @param value
     * @param message
     */
    private VerifyCodeTypeEnum(String value, String message) {
        this.value = value;
        this.message = message;
    }

    /**
     * 根据枚举类型的value获取枚举变量
     * 
     * @param value
     * @return
     */
    public static VerifyCodeTypeEnum getByValue(String value) {
        
        value = StringUtils.trimToEmpty(value);
        
        if (StringUtils.isBlank(value)) {
            return null;
        }
        for (VerifyCodeTypeEnum availableEnum : values()) {
            if (availableEnum.getValue().equals(value)){
                return availableEnum;
            }
        }
        return null;
    }

    public String getValue() {
        return value;
    }

    public String getMessage() {
        return message;
    }

}

