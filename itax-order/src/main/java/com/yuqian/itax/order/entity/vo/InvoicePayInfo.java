package com.yuqian.itax.order.entity.vo;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 办理开票支付信息
 */
@Getter
@Setter
public class InvoicePayInfo implements Serializable {

    private static final long serialVersionUID = -1L;

    /**
     * 支付方式
     */
    private String payWay;

    /**
     * 支付流水单号
     */
    private String payNo;

    /**
     * 所有税费
     */
    private BigDecimal allTax;

    /**
     * 服务费
     */
    private BigDecimal serviceFee;

    /**
     * 邮寄费
     */
    private BigDecimal postageFees;

    /**
     * 优惠金额
     */
    private BigDecimal discountAmount;

    /**
     * 支付金额
     */
    private BigDecimal payAmount;

    /**
     * 特价活动id
     */
    private Long discountActivityId;

}
