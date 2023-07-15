package com.yuqian.itax.park.entity.vo;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 税费规则配置
 * 
 * @Date: 2019年12月12日 18:40:35 
 * @author 蒋匿
 */
@Getter
@Setter
public class TaxRulesConfigVO implements Serializable {
	
	private static final long serialVersionUID = -1L;
	
	/**
	 * 主键id
	 */
	private Long id;
	
	/**
	 * 园区id 
	 */
	private Long parkId;
	
	/**
	 * 政策id
	 */
	private Long policyId;
	
	/**
	 * 企业类型 1-个体工商 2-个体独资 3-有限责任 4-有限合伙
	 */
	private Integer companyType;
	
	/**
	 * 税率
	 */
	private BigDecimal rate;
	
	/**
	 * 最小值
	 */
	private Long minAmount;
	
	/**
	 * 最大值
	 */
	private Long maxAmount;
	
	/**
	 * 税种类型 1-所得税 2-增值税
	 */
	private Integer taxType;
	
	/**
	 * 添加时间
	 */
	private Date addTime;
	
	/**
	 * 添加人
	 */
	private String addUser;
	
	/**
	 * 修改时间
	 */
	private Date updateTime;
	
	/**
	 * 修改人
	 */
	private String updateUser;

	/**
	 * 备注
	 */
	private String remark;

	/**
	 * 免额标记(1代表免额)
	 */
	private Integer isFree;

	/**
	 * 计税方式（1：预缴征收率，2：核定应税所得率）
	 */
	private Integer levyWay;

	/**
	 * 城建税税率
	 */
	private BigDecimal urbanConstructionTaxRate;
	/**
	 * 教育费附加税税率
	 */
	private BigDecimal educationSurchargeTaxRate;
	/**
	 * 地方教育附加税税率
	 */
	private BigDecimal localEducationSurchargeRate;

	/**
	 * 纳税人类型  1-小规模纳税人 2-一般纳税人
	 */
	private Integer taxpayerType;
}
