package com.yuqian.itax.order.entity.vo;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

/**
 *  @Author: Kaven
 *  @Date: 2020/3/25 15:54
 *  @Description: 开票订单返回bean-拓展宝
 */
@Getter
@Setter
public class InvoiceOrdVO implements Serializable {
	
	private static final long serialVersionUID = -1L;
	
	/**
	 * 订单号
	 */
	private String orderNo;

	/**
	 * 注册手机号
	 */
	private String regPhone;

	/**
	 * 订单状态 0-待创建 1-待付款 2-待审核 3-出票中 4-待发货 5-出库中 6-待收货 7-已签收  8-已取消
	 */
	private Integer orderStatus;

	/**
	 * 用户真实姓名
	 */
	private String realName;

	/**
	 * 会员等级
	 */
	private Integer levelNo;

	/**
	 * 开票企业名称
	 */
	private String companyName;

	/**
	 * 开票企业类型
	 */
	private Integer companyType;

	/**
	 * 发票类型 1-普通发票 2-增值税发票
	 */
	private Integer invoiceType;
	
	/**
	 * 创建时间
	 */
	private Date addTime;
	
	/**
	 * 用户邀请码
	 */
	private String inviteCode;

	/**
	 * 园区
	 */
	private String parkName;

	/**
	 * 开票金额
	 */
	private Long invoiceAmount;

	/**
	 * 优惠金额
	 */
	private Long discountAmount;

	/**
	 * 支付金额
	 */
	private Long payAmount;

	/**
	 * 开票服务费
	 */
	private Long serviceFee;

	/**
	 * 增值税费
	 */
	private Long vatFee;

	/**
	 * 个人所得税
	 */
	private Long personalIncomeTax;

	/**
	 * 快递费
	 */
	private Long postageFee;
}
