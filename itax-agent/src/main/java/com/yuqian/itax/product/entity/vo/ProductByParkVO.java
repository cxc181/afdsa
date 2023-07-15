package com.yuqian.itax.product.entity.vo;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;

@Getter
@Setter
public class ProductByParkVO implements Serializable {

    private Long id;

    /**
     * 园区id
     */
    private Long parkId;

    /**
     * 园区单独定价金额
     */
    private Long prodAmount;

    /**
     * 注销累计开票额度
     */
    private Long cancelTotalLimit;

    /**
     * 办理费（对公户独有）
     */
    private Long processingFee;


    /**
     * 金额名称
     */
    private String  amountName;
}
