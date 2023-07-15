package com.yuqian.itax.product.entity.vo;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 产品特价活动表
 * 
 * @Date: 2021年07月15日 15:46:44 
 * @author 蒋匿
 */
@Getter
@Setter
public class ProductDiscountActivityVO implements Serializable {
	
	private static final long serialVersionUID = -1L;

	/**
	 * 产品名称
	 */
	private String prodName;

	/**
	 * 产品金额
	 */
	private Long prodAmount;

	/**
	 * 产品类型
	 */
	private Integer prodType;

	/**
	 * 产品编码
	 */
	private String prodCode;

	/**
	 * 特价产品金额
	 */
	private Long specialPriceAmount;

	/**
	 * 个体户注销免费金额
	 */
	private Long cancelTotalLimit;

	/**
	 * 折扣
	 */
	private BigDecimal consumptionDiscount;

	/**
	 * 支付金额
	 */
	private Long payAmount;

	/**
	 * 人群标签id
	 */
	private Long crowdLabelId;

	/**
	 * 办理费（对公户申请独有）
	 */
	private Long processingFee;

	/**
	 * 特价活动产品id
	 */
	private Long discountActivityId;
}
