package com.yuqian.itax.product.entity.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 *  @Author: Kaven
 *  @Date: 2019/12/14 16:23
 *  @Description: 会员VIP前端展示Bean
 */
@Getter
@Setter
public class MemberVipVO implements Serializable {
	
	private static final long serialVersionUID = -1L;

	@ApiModelProperty(value = "产品ID",required = true)
	private Long productId;// 产品ID
	
	/**
	 * 产品名称
	 */
	@ApiModelProperty(value = "产品名称",required = true)
	private String prodName;
	
	/**
	 * 产品编码
	 */
	@ApiModelProperty(value = "产品编码",required = true)
	private String prodCode;
	
	/**
	 * 产品类型 1-个体开户 2-个独开户 3-有限合伙开户 4-有限责任开户  5-个体开票 6-个独开票 7-有限合伙开票 8-有限责任开票    9-税务顾问 10-城市服务商
	 */
	private Integer prodType;
	
	/**
	 * 金额
	 */
	@ApiModelProperty(value = "金额",required = true)
	private Long prodAmount;
	
	/**
	 * 金额名称
	 */
	private String amountName;
	
	/**
	 * 费用方式 1-固定金额 2-比率
	 */
	private Integer amountWay;
	
	/**
	 * 机构编码
	 */
	private String oemCode;
	
	/**
	 * 产品描述
	 */
	@ApiModelProperty(value = "产品描述")
	private String prodDesc;
	
	/**
	 * 状态 0-待上架 1-已上架 2-已下架 3-已暂停
	 */
	private Integer status;
	
	/**
	 * 备注
	 */
	private String remark;

	/**
	 * 会员折扣
	 */
	@ApiModelProperty(value = "会员折扣",required = true)
	private BigDecimal discount;

	/**
	 * 会员升级奖励比例
	 */
	@ApiModelProperty(value = "会员升级奖励比例",required = true)
	private BigDecimal membershipFee;

	/**
	 * 税务顾问分润比例
	 */
	@ApiModelProperty(value = "税务顾问分润比例",required = true)
	private BigDecimal profitsFirst;

	/**
	 * 城市服务商分润比例
	 */
	@ApiModelProperty(value = "城市服务商分润比例",required = true)
	private BigDecimal profitsSecond;

	/**
	 * 一级是城市服务商情况下的二级分润
	 */
	@ApiModelProperty(value = "一级是城市服务商情况下的二级分润",required = true)
	private BigDecimal profitsDiamondSecond;
}
