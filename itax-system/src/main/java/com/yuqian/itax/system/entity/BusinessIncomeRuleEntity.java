package com.yuqian.itax.system.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 经营所得适用个人所得税税率表
 *
 * @author yejian
 * @Date: 2020年11月12日 09:14:37
 */
@Getter
@Setter
@Table(name = "t_e_business_income_rule")
public class BusinessIncomeRuleEntity implements Serializable {

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
	 * 最小值
	 */
	private Long minAmount;

	/**
	 * 最大值
	 */
	private Long maxAmount;

	/**
	 * 税率
	 */
	private BigDecimal rate;

	/**
	 * 速算扣除数
	 */
	private Long quick;

}
