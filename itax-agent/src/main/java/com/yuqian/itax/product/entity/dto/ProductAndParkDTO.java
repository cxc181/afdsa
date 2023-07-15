package com.yuqian.itax.product.entity.dto;

import com.yuqian.itax.product.entity.ChargeStandardEntity;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class ProductAndParkDTO implements Serializable {

    private static final long serialVersionUID = -1L;

    /**
     * 园区产品定价id
     */
    private Long id;

    /**
     * 园区id
     */
    private Long parkId;

 	/**
     * 园区单独定价金额
     */
    private BigDecimal prodParkAmount;

    /**
     * 注销累计开票额度
     */
    private BigDecimal cancelTotalLimit;

    /**
     * 办理费（对公户独有）
     */
    private BigDecimal processingFee;

    /**
     * 园区单独定价收费标准集合
     */
    private List<ChargeStandardEntity> chargesByPark;
}
