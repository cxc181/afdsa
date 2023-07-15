package com.yuqian.itax.user.entity.vo;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

/**
 * @Description 企业对公户VO
 * @Author  Kaven
 * @Date   2020/9/7 11:13
*/
@Getter
@Setter
public class CompanyCorpAccountVO implements Serializable {
	
	private static final long serialVersionUID = -1L;
	/**
	 * 主键id
	 */
	private Long id;

	/**
	 * 企业名称
	 */
	private String companyName;

	/**
	 * 对公账户
	 */
	private String corporateAccount;

	/**
	 * 开户行
	 */
	private String bindBankName;

	/**
	 * 状态(1-正常  2-冻结 3-注销 4-过期)
	 */
	private Integer status;

	/**
	 * 过期时间（注册成功当天延迟一年）
	 */
	private Date expirationTime;

	/**
	 * 可用余额
	 */
	private Long availableAmount;

	/**
	 * 冻结余额
	 */
	private Long freezeAmount;

	/**
	 * 制单员编号
	 */
	private String voucherMemberCode;

	/**
	 * 委托项目编号
	 */
	private String entrustProjectCode;

	/**
	 * 过期状态 1-正常 2-即将过期 3-已过期
	 */
	private Integer overdueStatus;
}
