package com.yuqian.itax.tax.entity.vo;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 企业税单历史成本汇总VO
 * 
 * @Date: 2022/5/18
 * @author lmh
 */
@Getter
@Setter
public class TaxBilHistoryCostItemVO implements Serializable {

	private static final long serialVersionUID = -1L;

	/**
     * 企业id
	 */
	private Long companyId;

	/**
	 * 税款所属期年
	 */
	private Integer taxBillYear;

	/**
	 * 企业年度成本汇总集合
	 */
	private List<Map<String,Long>> costItems;

	/**
	 * 年度成本总金额
	 */
	private Long totalCostAmount;

}
