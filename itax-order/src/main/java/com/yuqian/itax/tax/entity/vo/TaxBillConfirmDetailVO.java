package com.yuqian.itax.tax.entity.vo;

import com.yuqian.itax.tax.entity.CompanyTaxBillEntity;
import com.yuqian.itax.user.entity.MemberCompanyEntity;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 企业税单确认详情表
 * 
 * @Date: 2022/3/11
 * @author lmh
 */
@Getter
@Setter
public class TaxBillConfirmDetailVO implements Serializable {

	private static final long serialVersionUID = -1L;

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
	 * 冻结税额
	 */
	private Long frozenTaxMoney;

	public TaxBillConfirmDetailVO() {

	}
	public TaxBillConfirmDetailVO(CompanyTaxBillEntity entity, MemberCompanyEntity company) {
		if (company != null) {
			this.companyName = company.getCompanyName();
			this.operatorName = company.getOperatorName();
		}
		this.taxBillYear = entity.getTaxBillYear();
		this.taxBillSeasonal = entity.getTaxBillSeasonal();
		this.alreadyTaxMoney = entity.getAlreadyTaxMoney();
		this.shouldTaxMoney = entity.getShouldTaxMoney();
		this.recoverableTaxMoney = entity.getRecoverableTaxMoney();
		this.supplementTaxMoney = entity.getSupplementTaxMoney();
		this.frozenTaxMoney = entity.getIncomeTaxYearFreezeAmount();
	}
}
