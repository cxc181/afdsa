package com.yuqian.itax.order.entity.vo;

import lombok.Getter;
import lombok.Setter;
import org.jeecgframework.poi.excel.annotation.Excel;

import java.io.Serializable;
import java.math.BigDecimal;

@Getter
@Setter
public class InvoiceOrderGoodsDetailVO implements Serializable {


    /**
     * 订单编号
     */
    @Excel(name = "订单编号")
    private String orderNo;

    /**
     * 税收分类编码
     */
    @Excel(name = "税收分类编码")
    private String taxClassificationCode;

    /**
     * 商品名称
     */
    @Excel(name = "商品名称")
    private String goodsName;

    /**
     * 商品单价
     */
    @Excel(name = "单价")
    private BigDecimal goodsPrice;

    /**
     * 规格型号
     */
    @Excel(name = "规格")
    private String goodsSpecification;

    /**
     * 商品数量
     */
    @Excel(name = "数量")
    private BigDecimal goodsQuantity;

    /**
     * 计量单位
     */
    @Excel(name = "单位")
    private String goodsUnit;

    /**
     * 税率
     */
    @Excel(name = "税率")
    private BigDecimal goodsTaxRate;

    /**
     * 总金额
     */
    @Excel(name = "总金额")
    private BigDecimal goodsTotalPrice;


    /**
     * 总税费
     */
    @Excel(name = "总税费")
    private BigDecimal goodsTotalTax;
}
