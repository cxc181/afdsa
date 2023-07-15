package com.yuqian.itax.park.entity.vo;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @Description 增值税/所得税返回bean（纯API）
 * @Author  Kaven
 * @Date   2020/7/31 10:07
*/
@Getter
@Setter
public class TaxRatesRulesVO implements Serializable {
	
	private static final long serialVersionUID = -1L;

	/**
	 * 税率
	 */
	private Long id;

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

}
