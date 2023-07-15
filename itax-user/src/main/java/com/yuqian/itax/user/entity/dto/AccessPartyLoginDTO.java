package com.yuqian.itax.user.entity.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.io.Serializable;

/**
 * @ClassName AccessPartyLoginDTO
 * @Description oem接入方登录DTO
 * @Author lmh
 * @Date 2021/8/5 10:01
 * @Version 1.0
 */
@Getter
@Setter
public class AccessPartyLoginDTO implements Serializable {
    private static final long serialVersionUID = -1L;

    /**
     * OEM机构编码
     */
    private String oemCode;

    /**
     * 接入方编码
     */
    @ApiModelProperty("接入方编码")
    private String accessPartyCode;

    /**
     * 会员账号
     */
    @Pattern(regexp = "^1\\d{10}$", message = "手机号不存在")
    @ApiModelProperty("会员账号（手机号）")
    private String account;

    /**
     * 操作来源 1-微信小程序 2-支付宝小程序 3-微信公众号
     */
    @ApiModelProperty("操作来源")
    private Integer sourceType;

    /**
     * jsCode,获取微信openId参数
     */
    @ApiModelProperty("微信jsCode")
    private String jsCode;

    /**
     * authCode，获取支付宝userId参数
     */
    @ApiModelProperty(value = "authCode")
    private String authCode;
}
