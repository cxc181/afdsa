package com.yuqian.itax.order.entity.vo;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;

@Setter
@Getter
public class GoodsDetailVO implements Serializable {
    private static final long serialVersionUID = -1L;
    /**
     * 商品名称
     */
    private String goodsName;
    /**
     * 规格型号
     */
    private String goodsSpecification;
    /**
     * 计量单位
     */
    private String goodsUnit;
    /**
     * 商品数量
     */
    private BigDecimal goodsQuantity;
    /**
     * 商品单价
     */
    private BigDecimal goodsPrice;
    /**
     * 总金额
     */
    private Long goodsTotalPrice;
    /**
     * 税率
     */
    private BigDecimal goodsTaxRate;
    /**
     * 总税费
     */
    private Long goodsTotalTax;
}
