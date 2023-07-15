package com.yuqian.itax.system.entity;

import io.swagger.annotations.ApiModelProperty;
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
 * 企业所得税税率表
 * 
 * @Date: 2022年09月26日 11:19:08 
 * @author 蒋匿
 */
@Getter
@Setter
@Table(name="t_e_company_income_rule")
public class CompanyIncomeRuleEntity implements Serializable {
	
	private static final long serialVersionUID = -1L;
	
	/**
	 * 主键id
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	/**
	 * 级数
	 */
	private Long level;
	
	/**
	 * 全年应纳税所得额最小值（分）
	 */
	private Long minAmount;
	
	/**
	 * 全年应纳税所得额最大值（分）
	 */
	private Long maxAmount;
	
	/**
	 * 税率
	 */
	private BigDecimal rate;


	/**
	 * 添加时间
	 */
	@ApiModelProperty(value = "添加时间")
	private Date addTime;

	/**
	 * 添加人
	 */
	@ApiModelProperty(value = "添加人")
	private String addUser;

	/**
	 * 修改时间
	 */
	@ApiModelProperty(value = "修改时间")
	private Date updateTime;

	/**
	 * 修改人
	 */
	@ApiModelProperty(value = "修改人")
	private String updateUser;

	/**
	 * 备注
	 */
	@ApiModelProperty(value = "备注")
	private String remark;
}
