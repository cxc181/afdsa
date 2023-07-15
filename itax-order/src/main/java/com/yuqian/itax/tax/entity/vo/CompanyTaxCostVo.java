package com.yuqian.itax.tax.entity.vo;

import com.yuqian.itax.tax.entity.CompanyTaxBillEntity;
import com.yuqian.itax.tax.entity.CompanyTaxCostItemEntity;
import com.yuqian.itax.user.entity.MemberCompanyEntity;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

/**
 * 企业税单成本项表
 * 
 * @Date: 2021年12月13日 20:50:26 
 * @author 蒋匿
 */
@Getter
@Setter
public class CompanyTaxCostVo implements Serializable {
	
	private static final long serialVersionUID = -1L;

	public CompanyTaxCostVo(){}

	public CompanyTaxCostVo(CompanyTaxBillEntity entity,MemberCompanyEntity company){
		this.companyTaxBillId = entity.getId();
		if (company != null) {
			this.companyName = company.getCompanyName();
			this.operatorName = company.getOperatorName();
		}
		this.invoiceMoney = entity.getInvoiceMoney();
		this.taxBillYear = entity.getTaxBillYear();
		this.taxBillSeasonal = entity.getTaxBillSeasonal();
		this.yearCostAmount = entity.getYearCostAmount();
		this.yearIncomeAmount = entity.getYearIncomeAmount();
		this.costItemImgs = entity.getCostItemImgs();
		this.quarterCostAmount = entity.getQuarterCostAmount();
	}

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
	 * 收入金额
	 */
	private Long invoiceMoney;

	/**
	 * 成本金额
	 */
	private Long yearCostAmount;

	/**
	 * 年度累计收入金额
	 */
	private Long yearIncomeAmount;

	/**
	 * 电子签名
	 */
	private String signImg;

	/**
	 * 本季累计成本
	 */
	private Long quarterCostAmount;

	/**
	 * 成本项图片
	 */
	private String costItemImgs;

	/**
	 * 成本项图片
	 */
	private List<String> costItemImgsList;

	/**
	 * 成本项明细
	 */
	private List<CompanyTaxCostItemEntity> costItemList;
}
