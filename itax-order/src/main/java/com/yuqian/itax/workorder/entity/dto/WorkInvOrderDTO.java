package com.yuqian.itax.workorder.entity.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 开票工单
 * 
 * @Date: 2019年12月07日 20:00:45 
 * @author 蒋匿
 */
@Getter
@Setter
public class WorkInvOrderDTO implements Serializable {
	
	private static final long serialVersionUID = -1L;
	
	/**
	 * 主键id
	 */
	@NotNull(message="主键不能为空")
	private Long id;

	/**
	 * 商品名称
	 */
	@Size(max = 20, message = "商品名称不能超过20个字")
	private String goodsName;

	/**
	 * 发票备注
	 */
	@Size(max = 140, message = "发票备注不能超过140个字")
	private String invoiceRemark;
	/**
	 * 税收分类简称
	 */
	private String taxClassificationAbbreviation;
	/**
	 * 商品名称
	 */
	@Size(max = 20, message = "商品名称不能超过20个字")
	private String taxGoodsName;

	/**
	 * 收票邮箱
	 */
	private String email;

	/**
	 * 基础类目id
	 */
	private Long categoryId;

	/**
	 * 增值税率
	 */
	private BigDecimal vatRate;

}
