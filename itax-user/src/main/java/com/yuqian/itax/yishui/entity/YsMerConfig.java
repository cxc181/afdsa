package com.yuqian.itax.yishui.entity;

import lombok.Data;

/**
 * ====== 测试环境 =======
 * 请求地址：https://testshuichou.zhuoyankeji.com
 * 企业编号：E19577405
 * 测试账号：国金测试
 * 登陆密码：123456
 * secret ：c7ad028333e9b00894c23e3c4835a2e3
 * aeskey : 33e9b00894c23e3c
 * <p>
 * 项目任务
 * crowd_id  : 12795
 * resolve_id : 2899
 */
@Data
public class YsMerConfig {

    /**
     * 易税请求的服务地址
     */
    private String domain;
    /**
     * 易税API对接用户账号
     */
    private String userName;
    /**
     * 易税API对接用户密码
     */
    private String password;
    /**
     * 易税API对接企业编码
     */
    private String enterpriseSn;
    /**
     * 签名加密secret
     */
    private String secret;
    /**
     * 数据解密秘钥
     */
    private String aseKey;

    /**
     * 项目ID
     */
    private Integer crowdId;

    /**
     * 任务ID
     */
    private Integer resolveId;

    /**
     * 银行接口mock标志： true - mock开启，false/未配置 = mock关闭
     */
    private boolean mockFlag = Boolean.FALSE;

    /**
     * 挡板标志 ： true - 挡板开启，false/未配置 - 挡板关闭
     */
    private boolean sideFlag = Boolean.FALSE;

    private String oemCode;

}
