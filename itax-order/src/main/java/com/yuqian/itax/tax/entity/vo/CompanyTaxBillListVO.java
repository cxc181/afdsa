package com.yuqian.itax.tax.entity.vo;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

/**
 * 园区税单查询
 * 
 * @Date: 2020年12月03日 10:36:14 
 * @author 蒋匿
 */
@Getter
@Setter
public class CompanyTaxBillListVO implements Serializable {
	
	private static final long serialVersionUID = -1L;
	
	/**
	 * 企业税单id
	 */
	private Integer companyTaxBillId;

	/**
	 * 园区id
	 */
	private Long parkId;

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
	 * 税单状态 0-待确认 1-待退税 2-待补税 3-正常 4-已退税 5-已补税
	 */
	private Integer taxBillStatus;

	/**
	 * 公司名称
	 */
	private String companyName;

	/**
	 * 经营者名称
	 */
	private String operatorName;

	/**
	 * 超时描述
	 */
	private String overTimeDesc;

	/**
	 * 是否还有完税凭证，0：没有，1：含有
	 */
	private String vouchersStatus;

	/**
	 * 累计开票金额
	 */
	private Long totalInvoiceAmount = 0L;

	/**
	 * 所得税征收方式 1-查账征收 2-核定征收
	 */
	private int incomeLevyType;

	/**
	 * 企业状态 1-正常 2-禁用 4-已注销 5-注销中
	 */
	private Integer companyStatus;
}
