package com.yuqian.itax.yishui.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * @author Administrator
 */
@Data
public class AddEmployeeReq implements Serializable {

    /**
     * 姓名
     */
    private String name;
    /**
     * 身份证号
     */
    private String cer_code;

    /**
     * 银行卡号
     */
    private String bank_code;

    /**
     * 银行卡预留手机号
     */
    private String mobile;

    /**
     * 固定值： 1
     */
    private Integer has_auth = 1;

    /**
     * 固定值： 1
     */
    private Integer source = 1;

    /**
     *  手写签名
     * 	auth=1 传用户的手写签名
     */
    private String sign_img;

    /**
     * 自由职业合作服务协议
     * auth=1 传可访问的HTTP地址 （可以是图片或者PDF文件）
     */
    private String protocol_img;

    /**
     * 诚信纳税承诺书、税务办理授权委托书
     * auth=1 传可访问的HTTP地址 （可以是图片或者PDF文件）
     */
    private String contract_img;

    /**
     * 认证方式 ：1 银联四要素认证
     *          2 电子牵四要素认证
     */
    private String auth;

    /**
     * 身份证正面照 HTTP地址
     * auth=2 采用电子牵四要素认证时候必传
     */
    private String cer_front_img;

    /**
     * 身份证反面照 HTTP地址
     * auth=2 采用电子牵四要素认证时候必传
     */
    private String cer_reverse_img;
}
