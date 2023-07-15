package com.yuqian.itax.order.entity.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

/**
 * 消费开票订单返回分页列表实体
 *
 * @author yejian
 * @Date: 2020年09月27日 11:22:33
 */
@Getter
@Setter
public class ConsumptionInvoiceOrderPageVO implements Serializable {

    private static final long serialVersionUID = -1L;

    /**
     * 订单号
     */
    @ApiModelProperty(value = "订单号")
    private String orderNo;

    /**
     * 订单状态： 0-待出票 1-出票中 2-已出票 3-出票失败
     */
    @ApiModelProperty(value = "订单状态： 0-待出票 1-出票中 2-已出票 3-出票失败")
    private Integer orderStatus;

    /**
     * 添加时间
     */
    @ApiModelProperty(value = "添加时间")
    private Date addTime;

    /**
     * 发票方式 1-纸质发票 2-电子发票
     */
    @ApiModelProperty(value = "发票方式 1-纸质发票 2-电子发票")
    private Integer invoiceWay;

    /**
     * 抬头公司名称
     */
    @ApiModelProperty(value = "抬头公司名称")
    private String companyName;

    /**
     * 开票金额
     */
    @ApiModelProperty(value = "开票金额")
    private Long invoiceAmount;

    /**
     * 电子发票地址
     */
    @ApiModelProperty(value = "电子发票地址")
    private String invoicePdfUrl;

    /**
     * 发票图片电子
     */
    @ApiModelProperty(value = "发票图片电子")
    private String invoiceImgs;

    /**
     * 发票图片电子
     */
    private String[] invoiceImgList;

}
