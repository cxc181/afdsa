package com.yuqian.itax.user.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;
/**
 * 企业对公户表
 * 
 * @Date: 2020年09月07日 09:13:58 
 * @author 蒋匿
 */
@Getter
@Setter
@Table(name="t_e_company_corporate_account")
public class CompanyCorporateAccountEntity implements Serializable {
	
	private static final long serialVersionUID = -1L;
	
	/**
	 * 主键id
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
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
	 * 对公账户银行名称
	 */
	private String corporateAccountBankName;
	
	/**
	 * 制单员编号
	 */
	private String voucherMemberCode;
	
	/**
	 * 委托项目编号(专线)
	 */
	private String entrustProjectCode;

	/**
	 * 项目用途编号(专线)
	 */
	private String projectUseCode;
	/**
	 * 委托项目编号(网金)
	 */
	private String entrustProjectCodeWj;

	/**
	 * 项目用途编号(网金)
	 */
	private String projectUseCodeWj;

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
	 * 添加时间
	 */
	private Date addTime;
	
	/**
	 * 添加人
	 */
	private String addUser;
	
	/**
	 * 修改时间
	 */
	private Date updateTime;
	
	/**
	 * 修改人
	 */
	private String updateUser;
	
	/**
	 * 备注
	 */
	private String remark;

	/**
	 * 银行总部名称
	 */
	private String headquartersName;

	/**
	 * 银行总部编号
	 */
	private String headquartersNo;

	/**
	 * 过期状态 1-正常 2-即将过期 3-已过期
	 */
	private Integer overdueStatus;

	/**
	 * 是否发送过期提醒通知 0-未发送 1-已发送
	 */
	private Integer isSendNotice;


	/**
	 * 提现限额单笔(分)
	 */
	private Long singleWithdrawalLimit;
	/**
	 * 提现限额单日(分)
	 */
	private Long dailyWithdrawalLimit;
}
