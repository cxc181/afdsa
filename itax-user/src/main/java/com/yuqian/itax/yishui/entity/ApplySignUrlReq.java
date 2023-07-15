package com.yuqian.itax.yishui.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * @author Administrator
 */
@Data
public class ApplySignUrlReq implements Serializable {

    /**
     * 用户ID
     */
    private Long professional_id;
    /**
     * 用户签署完成后跳转的地址 请确保地址GET方式返回200状态码
     */
    private String back_url;

    /**
     * 默认letsign (电子签地址)
     *    miniapp(申请小程序地址)
     */
    private String sign_type;

}
