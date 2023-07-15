package com.yuqian.itax.user.entity.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

/**
 *  @Author: Kaven
 *  @Date: 2020/5/13 11:18
 *  @Description: 第三方用户实名认证接收参数DTO
 */
@Getter
@Setter
public class ExtUserAuthDTO {
    /**
     *  手机号
     */
    @NotBlank(message = "手机号不能为空")
    private String mobile;

    /**
     *  身份证号码
     */
    @NotBlank(message = "身份证号码不能为空")
    private String idCardNo;

    /**
     *  用户姓名
     */
    @NotBlank(message = "用户姓名不能为空")
    private String userName;

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
}