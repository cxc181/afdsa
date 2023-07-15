package com.yuqian.itax.common.constants;

/**
 * 结果常量类
 * 
 * @author LiuXianTing
 */
public enum ResultConstants {
	/**
	 * 成功
	 */
	SUCCESS("0000", "成功"),
	
	/**
	 * 失败
	 */
	FAIL("0001", "失败"),
	
	/**
	 * 未登录
	 */
	NOT_LOGIN("0002", "未登录"),
	
	/**
	 * 系统服务未开通
	 */
	SERVICE_NOT_OPEN("0003", "未开通服务"),
	
	/**
	 * 系统服务到期
	 */
	SERVICE_EXPIRATION("0004", "系统服务过期"),
	
	/**
	 * 未实名
	 */
	NOT_REALNAME_AUTH("0005", "未实名"),
	
	/**
	 * 权限不足
	 */
	PERMISSION_DENIED("0006", "权限不足"),

	/**
	 * 系统异常
	 */
	SYSTEM_EXCEPTION("E0001", "系统异常"),

	/**
	 * 签名失败
	 */
	SIGN_EXCEPTION("S0001", "签名失败"),

	/**
	 * 签名失败
	 */
	PARAMS_EXCEPTION("P0001","必传参数为空"),

	/* 登录注册模块常量枚举 */
	STAFF_COUNT_LIMIT("S0002","员工数已达上限！可告知邀请人联系客服扩容"),
	STAFF_NAME_CANNOT_NULL("S0003","员工注册失败，姓名不能为空"),
	INVITOR_NO_PERMISSION("S0004","该邀请人无邀请权限，请更换！"),
	INVITOR_NOT_EXISTS("S0005", "邀请人信息不存在，请修改！"),
	INVITOR_FORBIDDEN("S007", "邀请人账户已禁用，请联系邀请人！"),
	INVITE_CODE_INVALID("S0006","邀请码格式不正确：必须为6到20位数字或字母的组合"),

	/* 用户模块常量枚举 */
	USER_NOT_EXISTS("U0001","用户不存在"),
	USER_ALREADY_REGIST("U0002","用户已注册"),

	/* 产品相关常量枚举 */
	PRODUCT_NOT_EXISTS("P0002","产品不存在"),

	/* 订单模块常量枚举 */
	SHOP_NAME_ONE_INVALID("D0001","备选字号一格式不正确，请输入2-6个汉字"),
	SHOP_NAME_TWO_INVALID("D0002","备选字号二格式不正确，请输入2-6个汉字"),

	/* 税单 */
	TAX_BILL_OVER_TIME("SD0001","该企业存在待补税的税单，请补缴税款后再申请开票！"),

	/* 通知管理 */
	ADD_NOTICE_FAILED("TZ0001", "新增通知失败"),
	;

    /**
	 * 结果值
	 */
	private String retCode;
	
	/**
	 * 结果信息
	 */
	private String retMsg;

	ResultConstants(String retCode, String retMsg) {
		this.retCode = retCode;
		this.retMsg = retMsg;
	}

	public String getRetCode() {
		return retCode;
	}

	public void setRetCode(String retCode) {
		this.retCode = retCode;
	}

	public String getRetMsg() {
		return retMsg;
	}

	public void setRetMsg(String retMsg) {
		this.retMsg = retMsg;
	}
}
