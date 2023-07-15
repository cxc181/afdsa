package com.yuqian.itax.user.entity.vo;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

/**
 * 企业对公户表
 *
 * @author yejian
 * @Date: 2020年09月10日 09:13:58
 */
@Getter
@Setter
public class CompanyCorporateAccountHandlerVO implements Serializable {

	private static final long serialVersionUID = -1L;

	/**
	 * 主键id
	 */
	private Long id;

	/**
	 * 企业id
	 */
	private Long companyId;

	/**
	 * 用户id
	 */
	private Long memberId;

	/**
	 * 对公户配置id
	 */
	private Long corporateAccountConfigId;

	/**
	 * 对公账户
	 */
	private String corporateAccount;

	/**
	 * 制单员编号
	 */
	private String voucherMemberCode;

	/**
	 * 委托项目编号
	 */
	private String entrustProjectCode;

	/**
	 * 项目用途编号
	 */
	private String projectUseCode;

	/**
	 * 绑定银行卡号
	 */
	private String bindBankCardNumber;

	/**
	 * 绑定的卡银行名称
	 */
	private String bindBankName;

	/**
	 * 绑定的卡银行编码
	 */
	private String bindBankCode;

	/**
	 * 预留手机号
	 */
	private String bindBankPhone;

	/**
	 * 状态(1-正常  2-冻结 3-注销 4-过期)
	 */
	private Integer status;

	/**
	 * 注销凭证（图片地址，多个之间用逗号分割）
	 */
	private String cancelCredentials;

	/**
	 * 过期时间（注册成功当天延迟一年）
	 */
	private Date expirationTime;

	/**
	 * 机构编码
	 */
	private String oemCode;

}
