package com.yuqian.itax.tax.entity.vo;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 企业预税单
 * 
 * @Date: 2020年12月03日 10:36:14 
 * @author 蒋匿
 */
@Getter
@Setter
public class CompanyTaxBillPrepareListVO implements Serializable {
	
	private static final long serialVersionUID = -1L;

	/**
	 * 企业id
	 */
	private Long companyId;

	/**
	 * 税款所属期年
	 */
	private Integer taxBillYear;

	/**
	 * 税款所属期季
	 */
	private Integer taxBillSeasonal;

	/**
	 * 已交税费（已缴税费）
	 */
	private Long alreadyTaxMoney = 0L;
	
	/**
	 * 总应纳税所得额（应缴税费）
	 */
	private Long shouldTaxMoney = 0L;
	
	/**
	 * 应退税费
	 */
	private Long recoverableTaxMoney = 0L;
	
	/**
	 * 应补税费
	 */
	private Long supplementTaxMoney = 0L;

	/**
	 * 公司名称
	 */
	private String companyName;

	/**
	 * 经营者名称
	 */
	private String operatorName;

	/**
	 * 累计开票金额
	 */
	private Long totalInvoiceAmount = 0L;
}
