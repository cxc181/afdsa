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
public class ParkTaxBillVO implements Serializable {
	
	private static final long serialVersionUID = -1L;
	/**
	 * 园区税单id
	 */
	private Long id;
	

	/**
	 * 企业税单id
	 */
	private Integer companyTaxBillId;

	/**
	 * 税款所属期年
	 */
	private Integer taxBillYear;
	
	/**
	 * 税款所属期季度
	 */
	private Integer taxBillSeasonal;
	/**
	 * 税款所属期(字符串)
	 */

	private String taxBillTime;
	/**
	 * 园区名称
	 */
	private String parkName;

	/**
	 * 园区id
	 */
	private String parkId;
	/**
	 * 本期开票企业
	 */
	private Integer invoiceCompanyNumber;
	/**
	 * 已上传企业
	 */
	private Integer uploadingCompanyNumber;

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
	 * 税单状态 0-待确认 1-解析中 2-待上传  3-已确认
	 */
	private Integer taxBillStatus;

	/**
	 * 生成时间
	 */
	private Date addTime;
	/**
	 * 完成时间
	 */
	private Date completeTime;
	/**
	 * 凭证状态 0-未上传 1-解析中 2-已上传3-待上传
	 */
	private Integer vouchersStatus;
	/**
	 * 应上传增值税凭证企业
	 */
	private Integer shouldUploadVatVouchersCompanyNumber;
	/**
	 * 应上传个税凭证企业
	 */
	private Integer shouldUploadIitVouchersCompanyNumber;
	/**
	 * 已上传增值税凭证企业
	 */
	private Integer alreadyUploadVatVouchersCompanyNumber;
	/**
	 * 已上传个税凭证企业
	 */
	private Integer alreadyUploadIitVouchersCompanyNumber;

	/**
	 * 作废/红冲企业
	 */
	private Integer cancellationCompany;

	/**
	 * 所得税征收方式 1-查账征收 2-核定征收
	 */
	private Integer incomeLevyType;

	/**
	 * 未确认成本企业
	 */
	private Integer unCost;

	public void setTaxBillYear(Integer taxBillYear) {
		this.taxBillYear=taxBillYear;
		this.taxBillTime=taxBillYear+"年"+(this.taxBillTime==null?"":this.taxBillTime);
	}
	public void setTaxBillSeasonal(Integer taxBillSeasonal) {
		this.taxBillSeasonal=taxBillSeasonal;
		this.taxBillTime=(this.taxBillTime==null?"":this.taxBillTime)+taxBillSeasonal+"季度";
	}

}
