package com.yuqian.itax.order.entity.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 企业开票税费计算返回VO
 * @Date: 2020年07月20日 10:05:12
 * @author yejian
 */
@Getter
@Setter
public class CompanyInvoiceTaxCalcApiVO implements Serializable {

	private static final long serialVersionUID = -1L;

	/**
	 * 开票金额
	 */
	@ApiModelProperty(value = "开票金额")
	private Long invoiceAmount;

	/**
	 * 合计所有税费（合计增值税税费+合计个人所得税税费+合计附加税税费）
	 */
	@ApiModelProperty(value = "合计所有税费（合计增值税税费+合计个人所得税税费+合计附加税税费）")
	private Long allTax;

	/**
	 * 增值税计税金额
	 */
	@ApiModelProperty(value = "增值税计税金额")
	private Long vatFeeQuota;

	/**
	 * 增值税税率
	 */
	@ApiModelProperty(value = "增值税税率")
	private BigDecimal vatFeeRate;

	/**
	 * 增值税税费
	 */
	@ApiModelProperty(value = "增值税费")
	private Long vatFee;

	/**
	 * 补缴增值税税费
	 */
	@ApiModelProperty(value = "补缴增值税税费")
	private Long vatPayment;

	/**
	 * 合计增值税税费（增值税税费+补缴增值税税费）
	 */
	@ApiModelProperty(value = "合计增值税税费（增值税税费+补缴增值税税费）")
	private Long allVatTax;

	/**
	 * 个人所得税计税金额
	 */
	@ApiModelProperty(value = "个人所得税计税金额")
	private Long personalIncomeTaxQuota;

	/**
	 * 个人所得税税费
	 */
	@ApiModelProperty(value = "个人所得税税费")
	private Long personalIncomeTax;

	/**
	 * 个人所得税税率
	 */
	@ApiModelProperty(value = "个人所得税税率")
	private BigDecimal personalIncomeTaxRate;

	/**
	 * 补缴个人所得税税费
	 */
	@ApiModelProperty(value = "补缴个人所得税税费")
	private Long incomeTaxPayment;

	/**
	 * 合计个人所得税税费（个人所得税税费+补缴个人所得税税费）
	 */
	@ApiModelProperty(value = "合计个人所得税税费（个人所得税税费+补缴个人所得税税费）")
	private Long allIncomeTax;

	/**
	 * 附加税税费
	 */
	@ApiModelProperty(value = "附加税税费")
	private Long surcharge;

	/**
	 * 附加税税率
	 */
	@ApiModelProperty(value = "附加税税率")
	private BigDecimal surchargeRate;

	/**
	 * 补缴附加税税费
	 */
	@ApiModelProperty(value = "补缴附加税税费")
	private Long surchargePayment;

	/**
	 * 合计附加税税费（附加税税费+补缴附加税税费）
	 */
	@ApiModelProperty(value = "合计附加税税费（附加税税费+补缴附加税税费）")
	private Long allSurchargeTax;

//	/**
//	 * 服务费
//	 */
//	@ApiModelProperty(value = "服务费")
//	private Long serviceFee;

}
