package com.yuqian.itax.tax.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
/**
 * 企业税单表
 * 
 * @Date: 2020年12月03日 10:36:23 
 * @author 蒋匿
 */
@Getter
@Setter
@Table(name="t_e_company_tax_bill")
public class CompanyTaxBillEntity implements Serializable {
	
	private static final long serialVersionUID = -1L;
	
	/**
	 * 
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	/**
	 * 园区税单id
	 */
	private Long parkTaxBillId;
	
	/**
	 * 税款所属期年
	 */
	private Integer taxBillYear;
	
	/**
	 * 税款所属期-季度
	 */
	private Integer taxBillSeasonal;
	
	/**
	 * 
	 */
	private Long parkId;
	
	/**
	 * 
	 */
	private Long companyId;
	
	/**
	 * 本期开票金额
	 */
	private Long invoiceMoney;
	
	/**
	 * 已交税费
	 */
	private Long alreadyTaxMoney;
	
	/**
	 * 总应纳税所得额
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
	 * 税单状态 0-待确认 1-待退税 2-待补税 3-正常 4-已退税 5-已补税 6-待核对   7-待填报成本 8-待申报 9-已作废
	 */
	private Integer taxBillStatus;
	
	/**
	 * 确认时间
	 */
	private Date affirmTime;
	
	/**
	 * 完成时间
	 */
	private Date completeTime;
	
	/**
	 * 创建时间
	 */
	private Date addTime;
	
	/**
	 * 创建人
	 */
	private String addUser;
	
	/**
	 * 更新时间
	 */
	private Date updateTime;
	
	/**
	 * 更新人
	 */
	private String updateUser;
	
	/**
	 * 备注
	 */
	private String remark;
	
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
	 * 计税方式（1：预缴征收率，2：核定应税所得率）
	 */
	private Integer incomeLevyWay;

	/**
	 * 应税所得率
	 */
	private BigDecimal taxableIncomeRate;
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
	 *超过15天未补交是否已经发短信提醒 1-已发送短信
	 */
	private Integer overTimeIsSms;
	/**
	 *个人所得税凭证图片 1-已发送短信
	 */
	private String iitVoucherPic;
	/**
	 *增值税凭证图片 1-已发送短信
	 */
	private String vatVoucherPic;
	/**
	 * 罚款凭证
	 */
	private String ticketPic;
	/**
	 *个人所得税凭证状态  1-未上传 2-已上传 3-无需上传
	 */
	private Integer iitVouchersStatus;
	/**
	 *增值税凭证凭证状态  1-未上传 2-已上传 3-无需上传
	 */
	private Integer vatVouchersStatus;
	/**
	 * 作废/红冲金额
	 */
	private Long cancellationAmount;

	/**
	 * 成本金额
	 */
	private Long yearCostAmount;

	/**
	 * 年度累计收入金额
	 */
	private Long yearIncomeAmount;

	/**
	 * 成本项图片
	 */
	private String costItemImgs;

	/**
	 *  无票收入金额（分）
	 */
	private Long ticketFreeIncomeAmount;

	/**
	 * 增值税扣除税费（分）
	 */
	private Long vatDeductionTaxfee;

	/**
	 * 附加税扣除税费（分）
	 */
	private Long additionalDeductionTaxfee;

	/**
	 * 个税扣除金额（分）
	 */
	private Long iitDeductionAmount;

	/**
	 * 本季累计成本金额（分）
	 */
	private Long quarterCostAmount;

	/**
	 * 季度累计收入金额（分）
	 */
	private Long quarterIncomeAmount;

	/**
	 * 用户签名
	 */
	private String signImg;

	/**
	 * 所得税征收方式 1-查账征收 2-核定征收
	 */
	private Integer incomeLevyType;

	/**
	 * 订单编号
	 */
	private String orderNo;

	/**
	 * 生成方式 1季度自动生成 2企业注销生成
	 */
	private Integer generateType;

	/**
	 * 本年应缴所得税（分）
	 */
	private Long yearPayableIncomeTax;

	/**
	 * 个税可退税额
	 */
	private Long incomeTaxRefundAmount;

	/**
	 * 个税年度冻结税额
	 */
	private Long incomeTaxYearFreezeAmount;

	/**
	 * 是否发送超时确认成本提醒通知 0-未发送 1-已发送
	 */
	private Integer isSendNotice;
}
