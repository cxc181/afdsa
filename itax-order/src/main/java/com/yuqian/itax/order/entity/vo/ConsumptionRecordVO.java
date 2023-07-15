package com.yuqian.itax.order.entity.vo;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

/**
 * @Description 消费订单VO
 * @Author  Kaven
 * @Date   2020/9/27 10:11
*/
@Getter
@Setter
public class ConsumptionRecordVO implements Serializable {
	
	private static final long serialVersionUID = -1L;

	private Long id;

	/**
	 * 订单类型  3-提现 4-代理提现 5 - 工商注册 6-开票 7-用户升级 8-工商注销 9-证件申请 10-对公户申请 11-对公户提现
	 */
	private Integer orderType;

	/**
	 * 订单号
	 */
	private String orderNo;

	/**
	 * 消费金额(分)(对应订单的服务费)
	 */
	private Long consumptionAmount;

	/**
	 * 订单完成时间
	 */
	private Date addTime;


}
