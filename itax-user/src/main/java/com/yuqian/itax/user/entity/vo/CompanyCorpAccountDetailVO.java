package com.yuqian.itax.user.entity.vo;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @Description 企业对公户详情VO
 * @Author  Kaven
 * @Date   2020/9/7 14:39
*/
@Getter
@Setter
public class CompanyCorpAccountDetailVO implements Serializable {
	
	private static final long serialVersionUID = -1L;
	/**
	 * 主键id
	 */
	private Long id;

	/**
	 * 账户名
	 */
	private String companyName;

	/**
	 * 开户行
	 */
	private String corporateAccountBankName;

	/**
	 * 银行账户
	 */
	private String corporateAccount;

	/**
	 * 银行名称
	 */
	private String bankName;

	/**
	 * 银行账户（脱敏）
	 */
	private String corporateAccountSens;

	/**
	 * 持卡人姓名
	 */
	private String operatorName;

	/**
	 * 身份证号码
	 */
	private String idCardNumber;

	/**
	 * 银行卡号
	 */
	private String bindBankCardNumber;

}
