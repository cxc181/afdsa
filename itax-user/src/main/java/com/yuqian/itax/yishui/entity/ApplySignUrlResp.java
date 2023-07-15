package com.yuqian.itax.yishui.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * @author Administrator
 */
@Data
public class ApplySignUrlResp extends YiShuiBaseResp implements Serializable {

    /**
     * 电子牵唯一标识 后续用于查询签署结果
     */
    private String transactionCode;
    /**
     * 协议跳转地址
     */
    private String signUrl;

    private String signCode;

    /**
     * 微信app
     */
    private String wx_appid;

    /**
     * 跳转的小程序地址
     */
    private String pages;

    /**
     * 小程序申请链接
     */
    private Long letsign_id;

}
