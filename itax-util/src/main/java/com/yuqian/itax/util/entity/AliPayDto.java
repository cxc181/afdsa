package com.yuqian.itax.util.entity;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @Description 支付宝支付传输bean
 * @Author  Kaven
 * @Date   2020/10/21 13:41
*/
@Getter
@Setter
public class AliPayDto implements Serializable {
    private String keyNum;

    private String servicePubKey;

    private String agentNo;// 机构号

    private String merNo;// 商户号

    private String signKey;// signKey

    private String postUrl;// 请求地址

    private String callbackUrl;// 回调地址

    private String tradeNo;// 交易订单号

    private String amount;// 支付金额

    private String ipAddr;// IP地址

    private String productName;// 商品名称

    private String appId;// appId

    private String buyerId;// 买家支付宝用户号

    private String buyerLogonId;// 买家支付宝账号

    private String appSecret;// appSecret

    private String appPrivateKey;// 小程序私钥

    private String alipayPublicKey;// 支付宝公钥

    private String productCode; //渠道产品编码
}