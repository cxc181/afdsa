package com.yuqian.itax.user.entity.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

/**
 *  @Author: Kaven
 *  @Date: 2020/2/17 14:06
 *  @Description: 用户实名认证接收参数DTO
 */
@Getter
@Setter
public class UserAuthDTO {

    /**
     * 用户id
     */
    private Long memberId;

    /**
     *  用户姓名
     */
    @NotBlank(message = "用户姓名不能为空")
    private String userName;

    /**
     *  身份证号码
     */
    @NotBlank(message = "身份证号码不能为空")
    private String idCardNo;

    /**
     *  身份证有效期
     */
    @NotBlank(message = "身份证有效期不能为空")
    private String expireDate;

    /**
     *  身份证正面照
     */
    @NotBlank(message = "身份证正面照不能为空")
    private String idCardFront;

    /**
     *  身份证反面照
     */
    @NotBlank(message = "身份证反面照不能为空")
    private String idCardBack;

    /**
     *  身份证地址
     */
    @NotBlank(message = "身份证地址不能为空")
    private String idCardAddr;

    /**
     * 是否为他人办理 0-本人办理 1-为他人办理
     */
    private Integer isOther;
}