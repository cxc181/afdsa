package com.yuqian.itax.user.entity.vo;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @Description 企业对公户到账银行卡信息VO
 * @Author  Kaven
 * @Date   2020/9/7 11:13
*/
@Getter
@Setter
public class CorporateAccountBankCardVO implements Serializable {
	
	private static final long serialVersionUID = -1L;

	/**
	 * 企业名称
	 */
	private String companyName;

	/**
	 * 银行名称
	 */
	private String bankName;

	/**
	 * 卡类型 1-储蓄卡 2-信用卡
	 */
	private Integer cardType;

	/**
	 * 银行卡logo
	*/
	private String logo;

	/**
	 * 可用余额
	 */
	private Long availableAmount;

	/**
	 * 银行卡号
	 */
	private String bankCardNumber;

	/**
	 * 单笔限额
	 */
	private Long singleLimit;

	/**
	 * 单日限额
	 */
	private Long dayLimit;
}
