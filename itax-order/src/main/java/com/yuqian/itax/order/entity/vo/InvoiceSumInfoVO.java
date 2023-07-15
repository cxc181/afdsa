package com.yuqian.itax.order.entity.vo;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 	统计开票总信息对象
 *  @Author: Kaven
 *  @Date: 2019/12/11 19:40
 *  @Description:
 */
@Getter
@Setter
public class InvoiceSumInfoVO implements Serializable {
	
	private static final long serialVersionUID = -1L;

	/**
	 * 开票总金额总计
	 */
	private Long invoiceAmount;

	/**
	 * 增值税费总计
	 */
	private Long vatFee;

	/**
	 * 个人所得税总计
	 */
	private Long personalIncomeTax;

	/**
	 * 服务费总计
	 */
	private Long serviceFee;

	/**
	 * 服务费优惠总计
	 */
	private Long serviceFeeDiscount;

	/**
	 * 增值税补缴
	 */
	private Long vatPayment;

	/**
	 * 附加税
	 */
	private Long surcharge;

	/**
	 * 附加税补缴
	 */
	private Long surchargePayment;

	/**
	 * 所得税补缴
	 */
	private Long incomeTaxPayment;
}
