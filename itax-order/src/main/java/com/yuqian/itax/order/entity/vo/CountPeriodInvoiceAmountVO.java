package com.yuqian.itax.order.entity.vo;


import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 周期内开票金额返回VO
 * @Date: 2020年07月17日 09:16:12
 * @author yejian
 */
@Getter
@Setter
public class CountPeriodInvoiceAmountVO implements Serializable {
	
	private static final long serialVersionUID = -1L;

	/**
	 * 开票金额
	 */
	@ApiModelProperty(value = "开票金额")
	private Long countAmountInvoiced;

	/**
	 * 增值税率
	 */
	@ApiModelProperty(value = "增值税率")
	private BigDecimal vATFeeRate;
}
