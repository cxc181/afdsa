package com.yuqian.itax.order.entity.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 税费明细VO
 * @Date: 2021/3/18
 */
@Getter
@Setter
public class InvoiceServiceFeeDetailVO implements Serializable {

	private static final long serialVersionUID = -1L;

    /**
     * 阶段金额(分)
     */
    private Long phaseAmount;

    /**
     * 适用费率
     */
    private BigDecimal feeRate;

    /**
     * 服务费(分)
     */
    private Long feeAmount;

}
