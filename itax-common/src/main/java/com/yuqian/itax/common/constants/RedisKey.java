package com.yuqian.itax.common.constants;

/**
 * redis key
 * 
 * @author 刘献廷
 */
public interface RedisKey {

	/**
	 * 用户登陆失败key
	 */
	String USER_LOGIN_FAIL_KEY = "user_login_fail_";

	/**
	 * 用户登陆锁定key
	 */
	String USER_LOGIN_LOCK_KEY = "user_login_lock_";
	
	/**
	 * 用户注册短信key
	 */
	String SMS_USER_REGISTER_KEY_SUFFER = "user_reg_sms_";

	/**
	 * 用户登录短信key
	 */
	String SMS_USER_LOGIN_KEY_SUFFER = "user_login_sms_";
	/**
	 * 用户修改密码短信key
	 */
	String SMS_UPDATE_PWD_KEY_SUFFER = "user_update_pwd_sms_";
	/**
	 * 用户短信计数
	 */
	String USER_SMS_COUNT_KEY = "user_sms_count";
	/**
	 * 用户重置密码短信key
	 */
	String SMS_RESET_PWD_KEY_SUFFER = "user_reset_pwd_sms_";
	/**
	 * 用户绑卡短信key
	 */
	String SMS_USER_BIND_CARD_KEY_SUFFER = "user_bind_card_sms_";

	/**
	 * 用户解绑短信key
	 */
	String SMS_USER_UN_BIND_CARD_KEY_SUFFER = "user_un_bind_card_sms_";

	/**
	 * 用户充值申请短信key
	 */
	String SMS_WALLET_RECHARGE_KEY_SUFFER = "wallet_recharge_sms_";
	
	/**
	 * 用户提现申请短信key
	 */
	String SMS_WALLET_WITHDRAW_KEY_SUFFER = "wallet_withdraw_sms_";

	/**
	 * 对公户提现申请短信key
	 */
	String SMS_CORP_ACCOUNT_WITHDRAW_KEY_SUFFER = "corp_account_withdraw_sms_";

	/**
	 * 后台提现申请短信key
	 */
	String SMS_WALLET_WITHDRAW_ADMIN_KEY_SUFFER = "wallet_withdraw_admin_sms_";

	/**
	 * 开票订单余额支付短信key
	 */
	String SMS_WALLET_BALANCE_PAY_KEY_SUFFER = "wallet_balance_pay_sms_";

	/**
	 * 登录login key
	 */
	String LOGIN_TOKEN_KEY = "login_token_";

	/**
	 * 登录工号WORKER_LOGIN_TOKEN_KEY
	 */
	String WORKER_LOGIN_TOKEN_KEY = "worker_login_token_key";

	/**
	 * 短信黑名单
	 */
	String SYS_SMS_IP_BLACK_LISTING = "sys_sms_ip_black_listing";

	/**
	 * 分润详情入账钱包锁
	 */
	String PROFIT_DETAIL_QUEUE_LOCK_KEY = "profit_detail_queue_lock_key_";

    /**
     * oem机构信息
     */
    String OEM_CODE_KEY = "oem_code_key_";

	/**
	 * 工商注册微信支付订单锁
	 */
	String REGISTER_ORDER_PAY_LOCK_KEY = "register_order_pay_lock_key_";

	/**
	 * 订单余额支付锁
	 */
	String ORDER_BALANCE_PAY_LOCK_KEY = "order_balance_pay_lock_key_";

	/**
	 * 开户绑卡redis锁
	 */
	String REGIST_ORDER_BIND_REDIS_KEY = "regist_order_bind_redis_key_";

	/**
	 * 开户派单redis锁
	 */
	String REGIST_ORDER_DISPATCH_REDIS_KEY = "regist_order_dispatch_redis_key_";

	/**
	 * 开户解绑银行卡redis锁
	 */
	String REGIST_ORDER_UN_BIND_REDIS_KEY = "regist_order_un_bind_redis_key_";

	/**
	 * 用户提现redis锁
	 */
	String USER_WITHDRAW_REDIS_KEY = "user_withdraw_redis_key_";

	/**
	 * 对公户提现redis锁
	 */
	String CORPORATE_ACCOUNT_WITHDRAW_REDIS_KEY = "corporate_account_withdraw_redis_key_";

	/**
	 * 消费开票订单redis锁
	 */
	String CONSUMPTION_INV_ORDER_REDIS_KEY = "consumption_inv_order_redis_key_";

	/**
	 * 用户充值redis锁
	 */
	String USER_RECHARGE_REDIS_KEY = "user_recharge_redis_key_";

	/**
	 * 用户后台提现redis锁
	 */
	String USER_WITHDRAW_ADMIN_REDIS_KEY = "user_withdraw_admin_redis_key_";

	/**
	 * 注册redis锁
	 */
	String REGIST_REDIS_KEY = "regist_redis_key_";
	/**
     * 实名认证redis锁
     */
    String MEMBER_AUTH_REDIS_KEY = "member_auth_redis_key_";

	/**
	 * 后台工单接单redis锁
	 */
	String ADMIN_WORK_ORDER_ACCEPT_REDIS_KEY = "admin_work_order_accept_redis_key_";

	/**
	 * 开票公司锁
	 */
	String LOCK_INVOICE_COMPANY = "lock_invoice_company_";

	/**
	 * 园区经营地址生成
	 */
	String LOCK_BUSINESS_ADDRESS_BY_PARK = "lock_business_address_by_park_";

	/**
	 * 微信token缓存
	 */
	String WECHAT_ACCESS_TOKEN_SUFFER = "wechat_access_token_";

	/**
	 * 用户后台代理提现redis锁
	 */
	String USER_WITHDRAW_AGENT_ADMIN_REDIS_KEY = "user_withdraw_agent_admin_redis_key_";

	/**
	 * 用户后台代理充值redis锁
	 */
	String USER_RECHARGE_AGENT_ADMIN_REDIS_KEY = "user_recharge_agent_admin_redis_key_";

	/**
	 * 邮寄费退款
	 */
	String INVOICE_ORDER_REFUND_POSTAGE_FEE_REDIS_KEY = "invoice_order_refund_postage_fee_redis_key_";

	/**
	 * 开票确认签收锁
	 */
	String LOCK_INVOICE_CONFIRM_RECEIPT = "lock_invoice_confirm_receipt_";

	/**
	 * 开票记录发票推送锁
	 */
	String LOCK_INVOICE_PUSH_BY_INVOICERECORDNO = "lock_invoice_push_by_invoicerecordno_";

	/**
	 * 兑换优惠券锁
	 */
	String EXCHANGE_REDIS_KEY = "exchange_redis_key_";

	/**
	 * oem机构接入方信息
	 */
	String OEM_ACCESSPARTY_CODE_KEY = "oem_accessparty_code_key_";

	/**
	 * 开票订单创建用户锁定
	 */
	String LOCK_INVOICE_CREATER_MEMBER_KEY = "lock_invoice_create_key_";

	/**
	 * 税单推送锁
	 */
	String LOCK_BILL_CONFIRMATION = "lock_bill_confirmation_";

	/**
	 * 创建注册预订单锁
	 */
	String LOCK_REG_PRE_ORDER_KEY = "lock_reg_pre_order_key";

	/**
	 * 添加企业核心成员锁
	 */
	String LOCK_CORE_PERSONNEL_KEY = "lock_core_personnel_key";

	/**
	 * 易税oken缓存
	 */
	String YISHUI_TOKEN_SUFFER = "yishui_token_";

	/**
	 * 易税签约
	 */
	String YISHUI_SIGN_KEY_SUFFER = "yishui_sign_key_suffer:";
}
