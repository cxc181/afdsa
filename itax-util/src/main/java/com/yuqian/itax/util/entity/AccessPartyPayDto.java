package com.yuqian.itax.util.entity;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 *  @Author: lmh
 *  @Date: 2021/8/13
 *  @Description: 接入方支付传输bean
 */ 
@Getter
@Setter
public class AccessPartyPayDto implements Serializable {
    private String keyNum;

    private String servicePubKey;

    private String agentNo;// 机构号

    private String merNo;// 商户号

    private String signKey;// signKey

    private String postUrl;// 请求地址

    private String callbackUrl;// 回调地址

    private String tradeNo;// 交易订单号

    private String amount;// 支付金额

    private String userIp;// 用户IP地址

    private String productName;// 商品名称

    private String appId;// appId

    private String openId;// openId

    private String appSecret;//appSecret

    private String channel;// 加解密方式

    private String productCode; //渠道产品编码

    private String buyerId;// 买家支付宝用户号
}