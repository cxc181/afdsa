package com.yuqian.itax.order.entity.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

/**
 * 开票成果补传订单
 */
@Getter
@Setter
public class InvoiceAchievementOrderInfoVO implements Serializable {

    private static final long serialVersionUID = -1L;

    /**
     * 订单号
     */
    @ApiModelProperty(value = "订单号")
    private String orderNo;


    /**
     * 开票公司名称
     */
    @ApiModelProperty(value = "开票公司名称")
    private String companyName;

    /**
     * 发票类型 1-增值税普通发票 2-增值税专用发票
     */
    @ApiModelProperty(value = "发票类型")
    private Integer invoiceType;

    /**
     * 开票金额
     */
    @ApiModelProperty(value = "开票金额")
    private Long invoiceAmount;

    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间")
    private Date addTime;

    /**
     * 签收时间
     */
    @ApiModelProperty(value = "签收时间")
    private Date completeTime;

    /**
     * 是否超时，1：未超时，2：超时
     */
    @ApiModelProperty(value = "是否超时，1：未超时，2：超时")
    private Integer waterTimeOut;

}
