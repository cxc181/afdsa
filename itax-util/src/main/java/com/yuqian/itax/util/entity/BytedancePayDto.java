package com.yuqian.itax.util.entity;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 *  @Author: 蒋匿
 *  @Date: 2021/9/18
 *  @Description: 字节跳动微信支付传输bean
 */ 
@Getter
@Setter
public class BytedancePayDto implements Serializable {

    private String appId;// appId

    private String appSecret;//appSecret

    private String paySalt; //支付密钥值

    private Long amount;// 支付金额

    private String tradeNo;// 交易订单号

    private String notifyUrl;// 回调地址

    private String openId; //用户openid

    private String orderNo; //订单号

    private String subject; //商品描述; 长度限制 128 字节，不超过 42 个汉字

    private String body; //商品详情

    private Long userId; //会员id

    private Integer orderType; //订单类型

    private String oemCode;

}