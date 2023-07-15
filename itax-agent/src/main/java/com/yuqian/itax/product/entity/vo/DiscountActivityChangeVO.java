package com.yuqian.itax.product.entity.vo;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;

@Getter
@Setter
public class DiscountActivityChangeVO implements Serializable {

    private static final long serialVersionUID = -1L;

    /**
     * 费用金额最小(分)
     */
    private Long chargeMin;

    /**
     * 费用金额最大(分)
     */
    private Long chargeMax;

    /**
     * 收费比率(百分比)
     */
    private BigDecimal chargeRate;
}
