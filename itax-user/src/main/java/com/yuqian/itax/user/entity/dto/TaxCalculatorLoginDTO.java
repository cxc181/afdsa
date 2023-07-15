package com.yuqian.itax.user.entity.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.io.Serializable;

/**
 * @ClassName TaxCalculationLoginDTO
 * @Description 税费测算登录DTO
 * @Author lmh
 * @Date 2022/9/26 15:32
 * @Version 1.0
 */
@Getter
@Setter
public class TaxCalculatorLoginDTO implements Serializable {
    private static final long serialVersionUID = -1L;

    /**
     * OEM机构编码
     */
    private String oemCode;

    /**
     * 会员账号
     */
    @Pattern(regexp = "^1\\d{10}$", message = "手机号不存在")
    private String account;

    /**
     * 邀请人账号
     */
    private String inviterAccount;

    /**
     * 短信验证码
     */
    @NotBlank(message = "短信验证码不能为空")
    private String verificationCode;
}
