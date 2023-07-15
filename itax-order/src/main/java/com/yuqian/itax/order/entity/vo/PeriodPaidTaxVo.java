package com.yuqian.itax.order.entity.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 企业已缴税费
 * 
 * @Date: 2021年07月16日
 */
@Getter
@Setter
public class PeriodPaidTaxVo implements Serializable {
	
	private static final long serialVersionUID = -1L;

	/**
	 * 已缴增值税
	 */
	@ApiModelProperty(value = "已缴增值税")
	private Long vatFee;

	/**
	 * 已缴附加税
	 */
	@ApiModelProperty(value = "已缴附加税")
	private Long surcharge;

	/**
	 * 已缴所得税
	 */
	@ApiModelProperty(value = "已缴所得税")
	private Long incomeTax;

	/**
	 * 年度累计成本金额（分）
	 */
	@ApiModelProperty(value = "年度累计成本金额（分）")
	private Long yearCostAmount;

	/**
	 * 个税扣除金额（分）
	 */
	@ApiModelProperty(value = "个税扣除金额（分）")
	private Long iitDeductionAmount;

	/**
	 * 当前周期应缴增值税
	 */
	private Long currentVatTax;

	/**
	 * 当前周期应缴所得税
	 */
	private Long currentIncomeTax;

	/**
	 * 本年历史已缴所得税
	 */
	private Long yearHistoryIncomeTax;
}