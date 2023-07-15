package com.yuqian.itax.user.entity.vo;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 *  @Author: Kaven
 *  @Date: 2020/6/3 9:54
 *  @Description: 会员升级规则前端展示VO
 */
@Getter
@Setter
public class MemberUpgradeRulesVO implements Serializable {
	
	private static final long serialVersionUID = -1L;
	
	/**
	 * 主键id
	 */
	private Long id;
	
	/**
	 * 会员等级 0-普通会员  1-VIP 2-白银会员 3-税务顾问 4-铂金会员 5-城市服务商
	 */
	private Integer levelNo;
	
	/**
	 * 机构编码
	 */
	private String oemCode;
	
	/**
	 * 个体注册数
	 */
	private Integer registCompanyNum;
	
	/**
	 * 开票最低金额
	 */
	private Long invoiceMinMoney;
	
	/**
	 * 开票达标个体数
	 */
	private Integer completeInvoiceCompanyNum;
	
	/**
	 * 是否支付升级 0-否 1-是
	 */
	private Integer isPayUpgrade = 0;
	
	/**
	 * 支付金额
	 */
	private Long payMoney;

	/**
	 * 达标个体数
	 */
	private Integer completeRegistCompanyNum;

	/**
	 * 达标开票数
	 */
	private Integer completeInvoiceNum;

	/**
	 * 直推达标个体数
	 */
	private Long extendStdComCount = 0L;

	/**
	 * 累计开票金额
	 */
	private Long totalInvoiceMoney = 0L;

	/**
	 * 累计直推开票
	 */
	private Long extendInvoiceAmount = 0L;

	/**
	 * 会费分润
	 */
	private BigDecimal membershipFee;

	/**
	 * 托管费分润率
	 */
	private BigDecimal profitsEntrustFeeRate;

	/**
	 * 下级是城市服务商时托管费分润率
	 */
	private BigDecimal profitsDiamondTwoEntrustFeeRate;

	/**
	 * 服务费分润率
	 */
	private BigDecimal serviceFeeRate;

	/**
	 * 下级是城市服务商时服务费分润率
	 */
	private BigDecimal profitsDiamondTwoServiceFeeRate;

	/**
	 * 下级是平级时会费分润率
	 */
	private BigDecimal profitsPeersTwoMembershipFee;

	/**
	 * 消费折扣
	 */
	private BigDecimal consumptionDiscount;

	/**
	 * 是否会费返还 0-否 1-是
	 */
	private Integer isReturnUpgrade = 0;

}
