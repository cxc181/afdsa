package com.yuqian.itax.agent.entity.vo;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

@Setter
@Getter
public class OemParamsVO implements Serializable {

    private static final long serialVersionUID = -1L;

    private Long id;
    /**
     * 机构名称
     */
    private String oemName;
    /**
     * 机构编码
     */
    private String oemCode;

    /**
     * 参数类型 1- 短信 2-微信支付 3-快捷支付
     */
    private Integer paramsType;
    /**
     * 账号
     */
    private String account;

    /**
     * 秘钥
     */
    private String secKey;
    /**
     * url地址
     */
    private String url;
    /**
     * 参数值
     */
    private String paramsValues;

    /**
     * 公钥
     */
    private String publicKey;

    /**
     * 私钥
     */
    private String privateKey;


    /**
     * 状态 0-不可用 1-可用
     */
    private Integer status;

    /**
     * 添加时间
     */
    private Date addTime;


    /**
     * 修改时间
     */
    private Date updateTime;
    /**
     * 备注
     */
    private String remark;

}
