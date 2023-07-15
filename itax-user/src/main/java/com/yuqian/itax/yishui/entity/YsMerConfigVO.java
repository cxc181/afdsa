package com.yuqian.itax.yishui.entity;

import lombok.Data;

/**
 * 易税渠道配置
 * @author zoumizi
 */
@Data
public class YsMerConfigVO {

    /**
     * 易税请求的服务地址
     */
    private String domain = "http://testshuichou.zhuoyankeji.com";
    /**
     * 易税API对接用户账号
     */
    private String userName = "国金测试";
    /**
     * 易税API对接用户密码
     */
    private String password = "123456";
    /**
     * 易税API对接企业编码
     */
    private String enterpriseSn = "E19577405";
    /**
     * 签名加密secret
     */
    private String secret = "c7ad028333e9b00894c23e3c4835a2e3";
    /**
     * 数据解密秘钥
     */
    private String aseKey = "33e9b00894c23e3c";

    /**
     * 项目ID
     */
    private Long crowdId = 12795L;

    /**
     * 任务ID
     */
    private Long resolveId = 2899L;

    /**
     * 银行接口mock标志： true - mock开启，false/未配置 = mock关闭
     */
    private Boolean mockFlag = Boolean.FALSE;

}
