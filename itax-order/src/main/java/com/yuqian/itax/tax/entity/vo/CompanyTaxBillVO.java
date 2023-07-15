package com.yuqian.itax.tax.entity.vo;

import com.yuqian.itax.tax.entity.CompanyTaxBillEntity;
import com.yuqian.itax.tax.enums.TaxBillStatusEnum;
import com.yuqian.itax.user.entity.MemberCompanyEntity;
import com.yuqian.itax.util.util.DateUtil;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 企业税单表
 * 
 * @Date: 2020年12月03日 10:36:23 
 * @author 蒋匿
 */
@Getter
@Setter
public class CompanyTaxBillVO implements Serializable {
	
	private static final long serialVersionUID = -1L;

	/**
	 * 主键id
	 */
	private Long companyTaxBillId;

	/**
	 * 公司名称
	 */
	private String companyName;

	/**
	 * 经营者名称
	 */
	private String operatorName;

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
	 * 增值税应缴增值税
	 */
	private Long vatAlreadyTaxMoney;
	
	/**
	 * 增值税已缴增值税
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
	 * 税单状态 0-待确认 1-待退税 2-待补税 3-正常 4-已退税 5-已补税
	 */
	private Integer taxBillStatus;

	/**
	 * 生成时间
	 */
	private String createTime;

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
	 * 应税所得率
	 */
	private BigDecimal taxableIncomeRate;
	/**
	 * 备注
	 */
	private String remark;

	/**
	 * 作废/红冲金额
	 */
	private Long cancellationAmount;

	/**
	 * 年度累计成本金额（分）
	 */
	private Long yearCostAmount;

	/**
	 * 年度不含税收入（分）
	 */
		private Long yearIncomeAmount;

	/**
	 * 所得税征收方式 1-查账征收 2-核定征收
	 */
	private int incomeLevyType;

	/**
	 * 无票收入金额（分）
	 */
	private Long ticketFreeIncomeAmount;

//	/**
//	 * 增值扣除税费（分）
//	 */
//	private Long vatDeductionTaxfee;
//
//	/**
//	 * 附加税扣除税费（分）
//	 */
//	private Long additionalDeductionTaxfee;

	/**
	 * 个税扣除金额（分）
	 */
	private Long iitDeductionAmount;

	/**
	 * 本季累计成本金额（分）
	 */
	private Long quarterCostAmount;

	/**
	 * 用户签名
	 */
	private String signImg;

	/**
	 * 冻结税额（分）
	 */
	private Long frozenTax;

	/**
	 * 个税可退税额
	 */
	private Long incomeTaxRefundAmount;


	public CompanyTaxBillVO() {

	}
	public CompanyTaxBillVO(CompanyTaxBillEntity entity, MemberCompanyEntity company) {
		this.companyTaxBillId = entity.getId();
		if (company != null) {
			this.companyName = company.getCompanyName();
			this.operatorName = company.getOperatorName();
		}
		this.taxBillYear = entity.getTaxBillYear();
		this.taxBillSeasonal = entity.getTaxBillSeasonal();
		this.invoiceMoney = entity.getInvoiceMoney();
		this.alreadyTaxMoney = entity.getAlreadyTaxMoney();
		this.shouldTaxMoney = entity.getShouldTaxMoney();
		this.recoverableTaxMoney = entity.getRecoverableTaxMoney();
		this.supplementTaxMoney = entity.getSupplementTaxMoney();
		this.vatTaxableIncomeAmount = entity.getVatTaxableIncomeAmount();
		this.vatRate = entity.getVatRate();
		this.vatAlreadyTaxMoney = entity.getVatAlreadyTaxMoney();
		this.vatShouldTaxMoney = entity.getVatShouldTaxMoney();
		this.vatRecoverableTaxMoney = entity.getVatRecoverableTaxMoney();
		this.vatSupplementTaxMoney = entity.getVatSupplementTaxMoney();
		this.taxBillStatus = entity.getTaxBillStatus();
		this.createTime = DateUtil.formatDefaultDate(entity.getAddTime());
		this.additionalTaxableIncomeAmount = entity.getAdditionalTaxableIncomeAmount();
		this.additionalRate = entity.getAdditionalRate();
		this.additionalAlreadyTaxMoney = entity.getAdditionalAlreadyTaxMoney();
		this.additionalShouldTaxMoney = entity.getAdditionalShouldTaxMoney();
		this.additionalRecoverableTaxMoney = entity.getAdditionalRecoverableTaxMoney();
		this.additionalSupplementTaxMoney = entity.getAdditionalSupplementTaxMoney();
		this.incomeTaxableIncomeAmount = entity.getIncomeTaxableIncomeAmount();
		this.incomeRate = entity.getIncomeRate();
		this.incomeAlreadyTaxMoney = entity.getIncomeAlreadyTaxMoney();
		this.incomeShouldTaxMoney = entity.getIncomeShouldTaxMoney();
		this.incomeRecoverableTaxMoney = entity.getIncomeRecoverableTaxMoney();
		this.incomeSupplementTaxMoney = entity.getIncomeSupplementTaxMoney();
		this.zpInvoiceAmount = entity.getZpInvoiceAmount();
		this.ppInvoiceAmount = entity.getPpInvoiceAmount();
		this.levyWay = entity.getIncomeLevyWay();
		this.taxableIncomeRate=entity.getTaxableIncomeRate()==null? new BigDecimal(0):entity.getTaxableIncomeRate();
		this.remark = entity.getRemark();
		this.cancellationAmount = entity.getCancellationAmount();
		this.incomeLevyType = entity.getIncomeLevyType();
		this.ticketFreeIncomeAmount = entity.getTicketFreeIncomeAmount();
/*		this.vatDeductionTaxfee = entity.getVatDeductionTaxfee();
		this.additionalDeductionTaxfee = entity.getAdditionalDeductionTaxfee();*/
		this.iitDeductionAmount = entity.getIitDeductionAmount();
		this.yearCostAmount = entity.getYearCostAmount();
		this.yearIncomeAmount = entity.getYearIncomeAmount();
		this.quarterCostAmount = entity.getQuarterCostAmount();
		this.signImg = entity.getSignImg();
		this.frozenTax = entity.getIncomeTaxYearFreezeAmount();
		this.incomeTaxRefundAmount = entity.getIncomeTaxRefundAmount();
	}
}
