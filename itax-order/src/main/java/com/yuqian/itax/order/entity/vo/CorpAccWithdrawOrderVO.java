package com.yuqian.itax.order.entity.vo;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

/**
 * @Description 对公户提现订单VO
 * @Author  Kaven
 * @Date   2020/9/9 15:28
*/
@Getter
@Setter
public class CorpAccWithdrawOrderVO implements Serializable {
	
	private static final long serialVersionUID = -1L;

	/**
	 * 订单类型
	 */
	private String orderType;

	/**
	 * 订单状态 0-待提交,1-支付、提现中,2-支付、提现完成,3-支付、提现失败,4-订单过期,5-已取消,6-待财务审核,7-财务审核失败
	 */
	private Integer orderStatus;

	/**
	 * 姓名（到账银行卡所有人姓名）
	 */
	private String operatorName;

	/**
	 * 订单号
	 */
	private String orderNo;

	/**
	 * 订单金额
	 */
	private Long orderAmount;

	/**
	 * 订单创建时间
	 */
	private Date addTime;

}
