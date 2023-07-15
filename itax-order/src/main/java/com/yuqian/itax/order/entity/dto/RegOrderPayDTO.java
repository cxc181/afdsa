package com.yuqian.itax.order.entity.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 *  @Author: Kaven
 *  @Date: 2019/12/12 10:29
 *  @Description: 工商注册订单支付接收DTO
 */
@Getter
@Setter
public class RegOrderPayDTO implements Serializable {
    private static final long serialVersionUID = -1L;

    @NotNull(message = "订单类型不能为空")
    @ApiModelProperty(value = "订单类型 1-充值 2-代理充值 3-提现 4-代理提现 5 - 工商开户 6-开票 7-用户升级",required = true)
    private Integer orderType;// 订单类型 1-充值 2-代理充值 3-提现 4-代理提现 5 - 工商开户 6-开票 7-用户升级

    @ApiModelProperty(value = "订单号，工商注册订单支付必传")
    private String orderNo;// 订单号

    @NotNull(message = "订单支付金额不能为空")
    @ApiModelProperty(value = "订单支付金额",required = true)
    private Long amount;// 订单支付金额

    @NotNull(message = "产品ID不能为空")
    @ApiModelProperty(value = "产品ID",required = true)
    private Long productId;// 产品ID

    @ApiModelProperty(value = "商品名称",required = true)
    private String productName;// 商品名称

    private Long currUserId;// 当前登录人ID

    private String oemCode;// OEM机构编码

    private String payChannel;// 支付通道 1-微信 2-余额 3-支付宝 4-易宝支付 5-建行 6-北京代付 7-纳呗 8-字节跳动支付

    private Integer sourceType;// 操作小程序来源 1-微信小程序 2-支付宝小程序 3-接入方 4-抖音

    private String buyerId;// 买家支付宝用户号

    private String buyerLogonId;// 买家支付宝账号

    private Long couponsIssueRecordId; // 优惠券发放记录id；
}