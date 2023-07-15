package com.yuqian.itax.tax.entity.vo;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 企业税单填报成本详情VO
 * 
 * @Date: 2022/5/18
 * @author lmh
 */
@Getter
@Setter
public class TaxBillFillCostDetailVO implements Serializable {

	private static final long serialVersionUID = -1L;

	/**
	 * 企业id
	 */
	private Long companyId;
	/**
	 * 纳税主体
	 */
	private String companyName;

	/**
	 * 税款所属期
	 */
	private String taxBillTime;

	/**
	 * 年度历史累计成本金额（分）
	 */
	private Long historyCostAmount;

	/**
	 * 年度不含税收入（分）
	 */
	private Long yearIncomeAmount;

	/**
	 * 年度个税扣除金额（分）
	 */
	private Long yearIitDeductionAmount;

	/**
	 * 本年已缴所得税（分）
	 */
	private Long yearIncomeAlreadyTaxMoney;

	/**
	 * 本年累计开票（分）
	 */
	private Long yearInvoiceAmount;

	/**
	 * 经营者
	 */
	private String operatorName;
}
