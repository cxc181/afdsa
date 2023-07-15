package com.yuqian.itax.agent.entity.po;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
@Setter
@Getter
public class OemParamsPO implements Serializable {

    private static final long serialVersionUID = -1L;

    String oemCode;

    Long id;
    /**
     * 参数类型 1- 短信 2-微信支付 3-快捷支付 4-北京代付
     */
    @NotNull(message="参数类型不能为空")
    Integer paramsType;
    /**
     * 账号
     */
    String account;
    /**
     * 秘钥
     */
    String secKey;
    /**
     * url地址
     */
    String url;
    /**
     * 参数值
     */
    String paramsValues;
    /**
     * 公钥
     */
    String publicKey;
    /**
     * 私钥
     */
    String privateKey;
    /**
     *状态 0-不可用 1-可用
     */
    Integer status;
    /**
     * 备注
     */
    String remark;
}
