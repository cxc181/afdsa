package com.yuqian.itax.api.annotation.lock;

/**
 * 业务属性枚举设定
 *
 * @date 2020/8/26
 */
public enum RedisLockTypeEnum {

    /**
     * 自定义 key 前缀
     */
    INVOICE_CREATE_ORDER("invoiceCreateOrderLock", "请勿重复点击创建开票订单操作！"),// 创建开票订单锁

    INVOICE_CONFIRM_ORDER("invoiceConfirmOrderLock", "请勿重复确认开票订单操作！"),// 确认开票订单锁

    BALANCE_PAY("balancePayLock", "请勿重复点击余额支付操作！"),// 余额支付锁

    INVOICE_CONFIRM_RECEIPT("invoiceConfirmReceipt", "请勿重复点击开票订单确认收货操作！"),// 开票订单确认收货锁

    INVOICE_PATCH_BANK_WATER("invoicePatchBankWater", "请勿重复点击开票订单补传流水操作！"),// 开票订单补传流水锁

    INVOICE_HEAD_INSERT("invoiceHeadInsert", "请勿重复点击添加发票抬头操作！"),// 添加发票抬头锁

    MEMBER_ADDRESS_EDIT("memberAddressEdit", "请勿重复点击！"),// 修改会员收件人信息

    USER_WITHDRAW("userWithdrawLock", "请勿重复点击提现操作！"),// 用户提现锁

    CORP_ACCOUNT_WITHDRAW_CREATE_ORDER("corpAccountWithdrawCreateOrder", "请勿重复点击对公户提现操作！"),// 对公户提现锁

    CORP_ACCOUNT_WITHDRAW_CONFIRM_ORDER("corpAccountWithdrawConfirmOrder", "请勿重复点击对公户提现确认操作！"),// 对公户提现订单确认锁

    COMPANY_COPORATE_APPLY_CREATE_ORDER("companyCoporateApplyCreateOrder", "请勿重复点击对公户申请操作！"),// 对公户申请订单锁

    USER_RECHARGE("userRechargeLock", "请勿重复点击充值操作！"),// 用户充值锁

    REGISTER_ORDER("registerOrderPayLock", "请勿重复点击微信支付操作！"),// 工商注册（会员升级）订单支付锁

    CERT_USE_OR_RETURN_ORDER("certUseOrReturnOrder", "请勿重复点击企业资源申请操作！"),// 企业资源申请锁

    CERT_USE_CONFIRM("certUseConfirm", "请勿重复点击确认收货操作！"),// 证件领用确认收货锁

    CONSUMPTION_INVOICE_CREATE_ORDER("consumptionInvoiceCreateOrder", "请勿重复点击创建消费开票订单操作！"),// 创建消费开票订单锁

    COMPANY_CANCELLATION_CREATE_ORDER("companyCancellationCreateOrder", "请勿重复点击企业注销操作！"),// 创建企业注销订单锁

    LOGIN_REGISTER("loginRegister", "请勿重复点击登陆注册操作！"),// 登陆注册锁

    COMMON_LOCK("commonLock:", "请勿重复点击！"),
    TAX_TO_BE_REFUNDED_LOCK("TAX_TO_BE_REFUNDED_LOCK", "请勿重复点击！"),
    TAX_TO_BE_PAID_LOCK("TAX_TO_BE_PAID_LOCK", "请勿重复点击！"),
    ;// 通用锁常量

    private String code;

    private String desc;

    RedisLockTypeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public String getUniqueKey(String key) {
        return String.format("%s:%s", this.getCode(), key);
    }
}
