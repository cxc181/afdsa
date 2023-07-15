package com.yuqian.itax.product.entity.vo;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

/**
 * 产品管理
 * 
 * @Date: 2019年12月07日 20:41:26 
 * @author 蒋匿
 */
@Getter
@Setter
public class ProductOemVO implements Serializable {
	
	private static final long serialVersionUID = -1L;
	
	/**
	 * 主键id
	 */
	private Long id;
	
	/**
	 * 产品名称
	 */
	private String prodName;
	
	/**
	 * 产品编码
	 */
	private String prodCode;
	
	/**
	 * 产品类型 1-个体开户 2-个独开户 3-有限合伙开户 4-有限责任开户  5-个体开票 6-个独开票 7-有限合伙开票 8-有限责任开票    9-税务顾问 10-城市服务商  17-对公户年费续费
	 */
	private Integer prodType;
	
	/**
	 * 产品描述
	 */
	private String prodDesc;
	
	/**
	 * 状态 0-待上架 1-已上架 2-已下架 3-已暂停
	 */
	private Integer status;
	
	/**
	 * 添加时间
	 */
	private Date addTime;
	
	/**
	 * 备注
	 */
	private String remark;

	/**
	 * 机构名称
	 */
	private String oemName;
	
}
