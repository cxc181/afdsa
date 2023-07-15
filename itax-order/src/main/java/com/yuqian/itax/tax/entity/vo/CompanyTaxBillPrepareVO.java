package com.yuqian.itax.tax.entity.vo;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 企业预税单详情
 * 
 * @Date: 2020年12月03日 10:36:23 
 * @author 蒋匿
 */
@Getter
@Setter
public class CompanyTaxBillPrepareVO implements Serializable {

	private static final long serialVersionUID = -1L;

	/**
	 * 企业id
	 */
	private Long companyId;

	/**
	 * 公司名称
	 */
	private String companyName;

	/**
	 * 经营者名称
	 */
	private String operatorName;

	/**
	 * 增值税减免周期 1-按月 2-按季度
	 */
	private Integer vatBreaksCycle;

	/**
	 * 个人所得税减免周期 1-按月 2-按季度
	 */
	private Long incomeTaxBreaksCycle;

	/**
	 * 税款所属期年
	 */
	private Integer taxBillYear;

	/**
	 * 税款所属期-季度
	 */
	private Integer taxBillSeasonal;

	/**
	 * 本期开票金额
	 */
	private Long invoiceMoney;

	/**
	 * 已交税费（已缴税费）
	 */
	private Long alreadyTaxMoney;

	/**
	 * 总应纳税所得额（应缴税费）
	 */
	private Long shouldTaxMoney;

	/**
	 * 应退税费
	 */
	private Long recoverableTaxMoney;

	/**
	 * 应补税费
	 */
	private Long supplementTaxMoney;

	/**
	 * 增值税应纳税所得额
	 */
	private Long vatTaxableIncomeAmount;

	/**
	 * 增值税适用税率
	 */
	private BigDecimal vatRate;

	/**
	 * 增值税已缴增值税
	 */
	private Long vatAlreadyTaxMoney;

	/**
	 * 增值税应缴增值税
	 */
	private Long vatShouldTaxMoney;

	/**
	 * 增值税应退增值税
	 */
	private Long vatRecoverableTaxMoney;

	/**
	 * 增值税应补增值税
	 */
	private Long vatSupplementTaxMoney;

	/**
	 * 附加税应纳税所得额
	 */
	private Long additionalTaxableIncomeAmount;

	/**
	 * 附加税适用税率
	 */
	private BigDecimal additionalRate;

	/**
	 * 附加税已缴增值税
	 */
	private Long additionalAlreadyTaxMoney;

	/**
	 * 附加税应缴增值税
	 */
	private Long additionalShouldTaxMoney;

	/**
	 * 附加税应退增值税
	 */
	private Long additionalRecoverableTaxMoney;

	/**
	 * 附加税应补增值税
	 */
	private Long additionalSupplementTaxMoney;

	/**
	 * 所得税应纳税所得额
	 */
	private Long incomeTaxableIncomeAmount;

	/**
	 * 所得税适用税率
	 */
	private BigDecimal incomeRate;

	/**
	 * 应税所得率
	 */
	private BigDecimal taxableIncomeRate;

	/**
	 * 所得税已缴增值税
	 */
	private Long incomeAlreadyTaxMoney;

	/**
	 * 所得税应缴增值税
	 */
	private Long incomeShouldTaxMoney;

	/**
	 * 所得税应退增值税
	 */
	private Long incomeRecoverableTaxMoney;

	/**
	 * 所得税应补增值税
	 */
	private Long incomeSupplementTaxMoney;

	/**
	 *增值税专用发票开票金额
	 */
	private Long zpInvoiceAmount;

	/**
	 * 增值税普通发票开票金额
	 */
	private Long ppInvoiceAmount;

	/**
	 * 计税方式（1：预缴征收率，2：核定应税所得率）
	 */
	private Integer levyWay;

	/**
	 * 跨季出票金额
	 */
	private Long moreQuarterInvoiceAmount = 0L;

}
