package com.yuqian.itax.order.entity.vo;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @Description 企业对公户提现订单详情VO
 * @Author  Kaven
 * @Date   2020/9/9 16:23
*/
@Getter
@Setter
public class CorpAccountWithdrawOrderDetailVO implements Serializable {
	
	private static final long serialVersionUID = -1L;

	/**
	 * 订单类型
	 */
	private Integer orderType;

	/**
	 * 订单金额
	 */
	private Long orderAmount;

	/**
	 * 订单状态 0-待提交,1-支付、提现中,2-支付、提现完成,3-支付、提现失败,4-订单过期,5-已取消,6-待财务审核,7-财务审核失败
	 */
	private Integer orderStatus;

	/**
	 * 订单号
	 */
	private String orderNo;

	/**
	 * 创建时间
	 */
	private Date addTime;

	/**
	 * 出款账户名
	 */
	private String registerName;

	/**
	 * 出款账户
	 */
	private String bindBankCardNumber;

	/**
	 * 收款账户名
	 */
	private String arriveUserName;

	/**
	 * 收款账户
	 */
	private String arriveBankAccount;

	/**
	 * 开票订单号
	 */
	private String invoiceOrderNo;

	/**
	 * 开票订单完成时间
	 */
	private Date completeTime;

	/**
	 * 抬头公司
	 */
	private String companyName;

	/**
	 * 使用额度
	 */
	private Long useAmount;

	/**
	 * 总额度
	 */
	private Long totalAmount;

    /**
     * 收款记录
     */
	private List<CorpAccountColsRecordsVO> collectionList;

}
