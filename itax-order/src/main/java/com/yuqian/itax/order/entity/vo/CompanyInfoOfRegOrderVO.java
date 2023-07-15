package com.yuqian.itax.order.entity.vo;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 *
 *  @Description: 注册订单企业信息
 */
@Getter
@Setter
public class CompanyInfoOfRegOrderVO implements Serializable {

	private static final long serialVersionUID = -1L;

	/**
	 * 订单号
	 */
	private String orderNo;

	/**
	 * 经营者姓名
	 */
	private String operatorName;

	/**
	 * 字号
	 */
	private String shopName;

	/**
	 * 备选字号1
	 */
	private String shopNameOne;

	/**
	 * 备选字号2
	 */
	private String shopNameTwo;

	/**
	 * 行业类型id
	 */
	private Long industryId;

	/**
	 * 行业类型
	 */
	private String industryName;

	/**
	 * 行业经验范围
	 */
	private String industryBusinessScope;

	/**
	 * 自选经验范围
	 */
	private String ownBusinessScope;

	/**
	 * 注册资本（万元）
	 */
	private BigDecimal registeredCapital;

	/**
	 * 纳税人类型  1-小规模纳税人 2-一般纳税人
	 */
	private Integer taxpayerType;

	/**
	 * 行业示例名称
	 */
	private String exampleName;

	/**
	 * 园区所属城市
	 */
	private String parkCity;

	/**
	 * 所属地区 1-江西
	 */
	private Integer affiliatingArea;

	/**
	 * 是否自动生成（企业信息） 0-否 1-是
	 */
	private Integer isAutoCreate;
}
