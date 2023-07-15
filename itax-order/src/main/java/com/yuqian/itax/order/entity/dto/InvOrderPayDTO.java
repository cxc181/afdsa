package com.yuqian.itax.order.entity.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 *  @Description: 开票订单余额支付DTO
 *  @Author: yejian
 *  @Date: 2019/12/12 10:29
 */
@Getter
@Setter
public class InvOrderPayDTO implements Serializable {

    private static final long serialVersionUID = -1L;

    @NotNull(message = "订单类型不能为空")
    @ApiModelProperty(value = "订单类型 1-开票 2-企业注销 3-证件领用 4-会员升级 5-对公户申请 6-补税 7-托管费续费 8-个体户注册", required = true)
    private Integer orderType;

    /**
     * 订单号
     */
    @NotNull(message="订单号不能为空")
    @ApiModelProperty(value = "订单号",required = true)
    private String orderNo;

    /**
     * 订单支付金额
     */
    @NotNull(message="订单支付金额不能为空")
    @ApiModelProperty(value = "订单支付金额",required = true)
    private Long amount;

    /**
     * 商品名称
     */
    @ApiModelProperty(value = "商品名称",required = true)
    private String goodsName;

    /**
     * 验证码
     */
    @ApiModelProperty(value = "验证码")
    private String verifyCode;

    /**
     * 优惠券发放记录id
     */
    @ApiModelProperty(value = "优惠券发放记录id")
    private Long couponsIssueRecordId;

    /**
     * 收款凭证
     */
    @ApiModelProperty(value = "收款凭证")
    private String receiptPaymentVoucher;

    /**
     * 操作人
     */
    @ApiModelProperty(value = "操作人")
    private String updateUser;
}