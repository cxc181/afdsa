package com.yuqian.itax.util.entity;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 *  @Author: 蒋匿
 *  @Date: 2021/9/18
 *  @Description: 字节跳动支付退款bean
 */ 
@Getter
@Setter
public class BytedanceRefundDto implements Serializable {

    private String appId;// appId

    private String appSecret;//appSecret

    private String paySalt; //支付密钥值

    private String tradeNo;// 原支付平台订单号

    private String refundOrderNo;//商户退款订单号

    private String reason; //退款原因

    private Long refundAmount; //退款金额，单位[分]

    private String callbackUrl;// 回调地址

    private String orderNo; //订单号

    private Long userId; //会员id

    private Integer orderType; //订单类型

    private String oemCode;//机构编码
}