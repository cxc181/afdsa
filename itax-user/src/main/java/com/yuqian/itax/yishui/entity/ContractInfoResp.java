package com.yuqian.itax.yishui.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author Administrator
 */
@Data
public class ContractInfoResp extends YiShuiBaseResp implements Serializable {

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
     * 签约开始时间
     */
    private Long contract_start_time;
    /**
     * 签约结束时间
     */
    private Long contract_end_time;

    /**
     * 微信昵称
     */
    private String nickname;

    /**
     * 微信头像地址
     */
    private String head_img;
    /**
     * 签约时间
     */
    private Long sign_time;

    /**
     * 微信openId
     */
    private String open_id;

    /**
     * 人员编号
     */
    private String professional_sn;

    /**
     * 是否认证 0未认证 1已认证
     */
    private Integer is_auth;

    /**
     * 是否签约 0未签约 1已签约
     */
    private Integer is_contract;

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
     * 人员ID
     */
    private Long professional_id;

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

    /**
     * 人员半身照
     */
    private String cer_face;


    /**
     * 签约ID（后续解约时候需要用到）
     */
    private Long enterprise_professional_facilitator_id;

    /**
     * 银行卡ID - 人员列表接口才会返回
     */
    private Long professional_bank_id;

    /**
     * 银行列表
     */
    private List<BankInfoDto> bank_lists;
}
