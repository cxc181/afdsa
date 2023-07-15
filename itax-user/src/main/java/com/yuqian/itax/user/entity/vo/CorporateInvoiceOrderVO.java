package com.yuqian.itax.user.entity.vo;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

/**
 * @Description 对公户提现-选择开票记录VO
 * @Author  Kaven
 * @Date   2020/9/7 11:13
*/
@Getter
@Setter
public class CorporateInvoiceOrderVO implements Serializable {
	
	private static final long serialVersionUID = -1L;

	/**
	 * 订单编号
	 */
	private String orderNo;

	/**
	 * 订单完成时间
	 */
	private Date completeTime;

	/**
	 * 抬头公司
	 */
	private String companyName;

	/**
	 * 开票金额
	 */
	private Long invoiceAmount;

	/**
	 * 剩余可提现额度
	 */
	private Long remainingWithdrawalAmount;
}
