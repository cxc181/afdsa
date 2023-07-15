package com.yuqian.itax.yishui.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * @author Administrator
 */
@Data
public class ContractSaveReq implements Serializable {

    /**
     * 签约ID
     */
    private Long enterprise_professional_facilitator_id;

    /**
     * 姓名
     */
    private String name;

    /**
     * 银行卡预留手机号
     */
    private String mobile;
    /**
     * 身份证号
     */
    private String cer_code;

    /**
     * 银行卡号
     */
    private String bank_code;

    /**
     * 身份证正面照 HTTP地址
     */
    private String cer_front_img;

    /**
     * 身份证反面照 HTTP地址
     */
    private String cer_reverse_img;

    /**
     *  手写签名
     */
    private String sign_img;

    /**
     * 人员半身照URL （如果未涉及到大额业务可留空）
     */
    private String cer_face = "";

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

}
