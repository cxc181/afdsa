package com.yuqian.itax.product.entity.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 产品特价活动
 * 
 * @Date: 2019年12月07日 20:41:26 
 * @author 蒋匿
 */
@Getter
@Setter
public class ProductDiscountActivityAPIDTO implements Serializable {
	
	private static final long serialVersionUID = -1L;

	/**
	 * 产品类型 1-个体开户 5-个体开票 11-个体注销  15-公户申请和托管 16-个体托管费续费
	 */
	private Integer productType;

	/**
	 * 行业id
	 */
	private Long industryId;

	/**
	 * 园区id
	 */
	private Long parkId;

	/**
	 * 企业id
	 */
	private Long companyId;

	/**
	 * 会员id
	 */
	private Long memberId;

	/**
	 * 机构编码
	 */
	private String oemCode;
}
