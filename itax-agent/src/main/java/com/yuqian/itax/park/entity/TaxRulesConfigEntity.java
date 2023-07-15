package com.yuqian.itax.park.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
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
@Table(name="t_e_tax_rules_config")
public class TaxRulesConfigEntity implements Serializable {
	
	private static final long serialVersionUID = -1L;
	
	/**
	 * 主键id
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
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
	 * 企业类型 1-个体工商 2-个体独资 3-有限合伙 4-有限责任
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
	 * 税种类型 1-所得税 2-增值税 3-附加税 4-印花税 5-水利建设基金
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
	 * 行业类型id
	 */
	private Long industryId;
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
	 * 是否允许开普票 0-不允许 1-允许
	 */
	private Integer isOpenPp;

	/**
	 * 是否允许开专票 0-不允许 1-允许
	 */
	private Integer isOpenZp;

	/**
	 * 纳税人类型  1-小规模纳税人 2-一般纳税人
	 */
	private Integer taxpayerType;
	
}
