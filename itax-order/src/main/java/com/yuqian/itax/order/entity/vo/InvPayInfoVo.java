package com.yuqian.itax.order.entity.vo;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 办理核名支付信息
 */
@Getter
@Setter
public class InvPayInfoVo implements Serializable {

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
     * 订单金额
     */
    private BigDecimal orderAmount;

    /**
     * 优惠金额
     */
    private BigDecimal discountAmount;

    /**
     * 优惠券金额
     */
    private BigDecimal faceAmount;

    /**
     * 支付金额
     */
    private BigDecimal payAmount;

    /**
     * 产品特价活动id
     */
    private Long discountActivityId;

}
