package com.yuqian.itax.park.entity.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * @Description 园区列表返回VO（纯API）
 * @Author  Kaven
 * @Date   2020/7/31 10:00
*/
@Getter
@Setter
public class Park4OutVO implements Serializable {
	
	private static final long serialVersionUID = -1L;
	
	/**
	 * 园区名称
	 */
	@ApiModelProperty(value = "园区名称")
	private String parkName;

	/**
	 * 园区编码
	 */
	@ApiModelProperty(value = "园区编码")
	private String parkCode;

	/**
	 * 所属城市
	 */
	@ApiModelProperty(value = "所属城市")
	private String parkCity;

	/**
	 * 增值税个税减免额度
	 */
	@ApiModelProperty(value = "增值税个税减免额度")
	private Long vatBreaksAmount;

	/**
	 * 增值税个税减免周期
	 */
	@ApiModelProperty(value = "增值税个税减免周期")
	private Integer vatBreaksCycle;

	/**
	 * 个税优惠政策
	 */
	@ApiModelProperty(value = "个税优惠政策")
	private String incomeTaxBreaksAmount;

	/**
	 * 所得税减免周期
	 */
	@ApiModelProperty(value = "所得税减免周期")
	private Integer incomeTaxBreaksCycle;

	/**
	 * 年度开票总额
	 */
	@ApiModelProperty(value = "年度开票总额")
	private Long totalInvoiceAmount;

	/**
	 * 政策文件地址
	 */
	@ApiModelProperty(value = "政策文件地址")
	private String policyFileUrl;

	/**
	 * 增值税率
	 */
	@ApiModelProperty(value = "增值税率")
	private List<TaxRatesRulesVO> vatFeeRates;

	/**
	 * 附加税率
	 */
	@ApiModelProperty(value = "附加税率")
	private BigDecimal surchargeRate;

	/**
	 * 所得税率
	 */
	@ApiModelProperty(value = "所得税率")
	private List<TaxRatesRulesVO> personalIncomeTaxRates;

	/**
	 * 园区详细地址
	 */
	@ApiModelProperty(value = "园区详细地址")
	private String parkAddress;
}
