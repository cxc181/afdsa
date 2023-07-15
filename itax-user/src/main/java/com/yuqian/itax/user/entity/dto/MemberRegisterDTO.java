package com.yuqian.itax.user.entity.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @Description 会员注册同步接收dto（云租适用）
 * @Author  Kaven
 * @Date   2020/6/23 3:51 下午
*/
@Getter
@Setter
public class MemberRegisterDTO implements Serializable {
    private static final long serialVersionUID = -1L;

    /**
     * OEM机构编码
     */
    @ApiModelProperty(value = "OEM机构编码")
    private String oemCode;

    /**
     * 手机号
     */
    @NotBlank(message = "手机号不能为空")
    @ApiModelProperty(value = "手机号")
    private String mobile;

    /**
     * 用户姓名
     */
    @NotBlank(message = "用户姓名不能为空")
    @ApiModelProperty(value = "用户姓名")
    private String userName;
    /**
     * 身份证正面
     */
    @NotBlank(message = "身份证正面照不能为空")
    @ApiModelProperty(value = "身份证正面",required = true)
    private String idCardFront;

    /**
     * 身份证反面
     */
    @NotBlank(message = "身份证反面照不能为空")
    @ApiModelProperty(value = "身份证反面",required = true)
    private String idCardReverse;

    /**
     * 身份证号码
     */
    @NotBlank(message = "身份证号码不能为空")
    @ApiModelProperty(value = "身份证号码",required = true)
    private String idCardNumber;

    /**
     * 身份证有效期
     */
    @NotBlank(message = "身份证有效期不能为空")
    @ApiModelProperty(value = "身份证有效期",required = true)
    private String expireDate;

    /**
     * 身份证地址
     */
    @NotBlank(message = "身份证地址不能为空")
    @ApiModelProperty(value = "身份证地址",required = true)
    private String idCardAddr;

    /**
     * jsCode，获取微信openId参数
     */
//    @NotBlank(message = "jsCode不能为空")
//    @ApiModelProperty(value = "jsCode")
    private String jsCode;

    /**
     * 邀请人账号
     */
    @NotNull(message = "邀请人账号不能为空")
    @ApiModelProperty(value = "邀请人账号",required = true)
    private String inviterAccount;

    /**
     * 省编码
     */
    @NotNull(message = "省编码不能为空")
    @ApiModelProperty(value = "省编码")
    private String provinceCode;

    /**
     * 市编码
     */
    @NotNull(message = "市编码不能为空")
    @ApiModelProperty(value = "市编码")
    private String cityCode;

}