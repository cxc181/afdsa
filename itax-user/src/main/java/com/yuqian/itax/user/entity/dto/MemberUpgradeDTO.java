package com.yuqian.itax.user.entity.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.*;
import java.io.Serializable;

/**
 * 会员升级接收dto
 * @author：pengwei
 * @Date：2019/12/25 18:39
 * @version：1.0
 */
@Getter
@Setter
public class MemberUpgradeDTO implements Serializable {
    private static final long serialVersionUID = -1L;

    /**
     * 会员主键
     */
    @NotNull(message = "会员主键不能为空")
    private Long id;

    /**
     * 持卡人姓名
     */
    @Size(max = 30, message = "持卡人姓名不能超过30位字符")
    private String userName;

    /**
     * 身份证号
     */
    @Size(max = 18, message = "身份证号不能超过18位字符")
    private String idCard;

    /**
     * 预留手机号
     */
    @Size(max = 11, message = "预留手机号不能超过11位字符")
    private String phone;

    /**
     * 银行卡号
     */
    @Size(max = 30, message = "银行卡号不能超过30位字符")
    private String bankNumber;

    /**
     * 备注
     */
    @Size(max = 30, message = "备注不能超过30位字符")
    private String remark;

    /**
     * 会员升级等级：1-VIP 2-白银会员 3-税务顾问 4-铂金会员 5-城市服务商
     */
    @NotNull(message="会员升级等级不能为空")
    @Min(value = 1, message = "会员升级等级有误")
    @Max(value = 1, message = "会员升级等级有误")
    private Integer levelNo;

    /**
     * 身份证正面照
     */
    @Size(max = 255, message = "身份证正面照不能超过255位字符")
    private String idCardFront;

    /**
     *  身份证反面照
     */
    @Size(max = 255, message = "身份证反面照不能超过255位字符")
    private String idCardBack;

    /**
     * 身份证有效期
     */
    @Pattern(regexp = "^$|((([0-9]{3}[1-9]|[0-9]{2}[1-9][0-9]{1}|[0-9]{1}[1-9][0-9]{2}|[1-9][0-9]{3}).(((0[13578]|1[02]).(0[1-9]|[12][0-9]|3[01]))|((0[469]|11).(0[1-9]|[12][0-9]|30))|(02.(0[1-9]|[1][0-9]|2[0-8]))))|((([0-9]{2})(0[48]|[2468][048]|[13579][26])|((0[48]|[2468][048]|[3579][26])00)).02.29))-(长期|(([0-9]{3}[1-9]|[0-9]{2}[1-9][0-9]{1}|[0-9]{1}[1-9][0-9]{2}|[1-9][0-9]{3}).(((0[13578]|1[02]).(0[1-9]|[12][0-9]|3[01]))|((0[469]|11).(0[1-9]|[12][0-9]|30))|(02.(0[1-9]|[1][0-9]|2[0-8]))))|((([0-9]{2})(0[48]|[2468][048]|[13579][26])|((0[48]|[2468][048]|[3579][26])00)).02.29))", message = "身份证有效期格式有误，格式要求：yyyy.MM.dd-yyyy.MM.dd")
    private String expireDate;

    /**
     * 身份证地址
     */
    @Size(max = 128, message = "身份证地址不能超过128位字符")
    private String idCardAddr;
}
