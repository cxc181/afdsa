package com.yuqian.itax.user.entity.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 *  @Author: Kaven
 *  @Date: 2019/12/19 14:56
 *  @Description: 会员登录接收dto
 */
@Getter
@Setter
public class MemberLoginDTO implements Serializable {
    private static final long serialVersionUID = -1L;

    /**
     * OEM机构编码
     */
    private String oemCode;

    /**
     * 会员账号
     */
    @NotBlank(message = "会员账号不能为空")
    @ApiModelProperty(value = "会员账号（手机号）")
    private String account;

    /**
     * 会员昵称
     */
    @ApiModelProperty(value = "会员昵称")
    private String memberName;

    /**
     * 注册会员类型
     */
    @ApiModelProperty(value = "注册会员类型：0普通会员 -1员工")
    private Integer memberType;

    /**
     * 验证码
     */
    @NotBlank(message = "验证码不能为空")
    @ApiModelProperty(value = "验证码")
    private String verifyCode;

    /**
     * 邀请人账号，注册时必传
     */
    @ApiModelProperty(value = "邀请人账号，注册时必传",required = true)
    private String inviterAccount;

    /**
     * jsCode，获取微信openId参数
     */
    @NotBlank(message = "jsCode不能为空")
    @ApiModelProperty(value = "jsCode")
    private String jsCode;

    /**
     * authCode，获取支付宝userId参数
     */
    @ApiModelProperty(value = "authCode")
    private String authCode;

    /**
     * 操作小程序来源 1-微信小程序 2-支付宝小程序 3-接入方 4-字节跳动
     */
    @ApiModelProperty(value = "操作小程序来源 1-微信小程序 2-支付宝小程序 3-接入方 4-字节跳动", required = true)
    private Integer sourceType;

    /**
     * 国金助手编码
     */
    private String channelCode;

    /**
     * 字节跳动匿名用户code
     */
    private String anonymousCode;

    /**
     * 收单机构编码
     */
    private String otherPayOemCode;
}