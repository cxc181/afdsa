package com.yuqian.itax.user.entity.po;


import com.yuqian.itax.util.validator.Add;
import com.yuqian.itax.util.validator.Update;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class AgentPO  implements Serializable {
    private static final long serialVersionUID = -1L;

    /**
     * id
     */
    private  Long id;
    /**
     * 机构代码
     */
    @NotBlank(message = "请选择正确的所属机构")
    private String oemCode;
    /**
     * 平台类型  1-平台 2-机构 3-园区 4-城市合伙人 5-城市合伙人
     */
    @Min(value = 4,message = "平台类型错误")
    @Max(value = 5,message = "平台类型错误")
    private Integer platformType;
    /**
     * 名称
     */
    @NotBlank(message = "请输入代理商名称")
    private String nickname;
    /**
     * 账号
     */
    @NotBlank(message = "请输入代理商账号" )
    @Size(min = 6, max = 20, message = "账号不能小于6位字符，超过20位字符")
    @Pattern(regexp = "^[a-zA-Z0-9_]{8,16}$", message = "请输入8~16位，支持字母/数字/下划线组合")
    private String username;
    /**
     * 绑定手机号
     */
    @NotBlank(message = "请输入代理商绑定手机号")
    private String phone;

    /**
     * 所属城市
     */
    @NotBlank(message = "请选择代理商所属城市")
    private String cityCode;
    /**
     * 所属省
     */
    @NotBlank(message = "请选择代理商所属城市")
    private String provinceCode;
    /**
     * 推广账号手机号
     */
    @NotBlank(message = "请输入代理商推广账号手机号")
    private String bindingAccount;

    /**
     * 会费分润率
     */
    @NotNull(message = "请输入代理商会费分润率")
    private BigDecimal membershipFee;
    /**
     * 开户分润率
     */
    @NotNull(message = "请输入代理商开户分润率")
    private BigDecimal registerFee;
    /**
     * 开票分润率
     */
    @NotNull(message = "请输入代理商开票分润率")
    private BigDecimal invoiceFee;

    /**
     * 企业注销服务费分润率
     */
    @NotNull(message = "请输入代理商企业注销服务费分润率")
    private BigDecimal cancelCompanyFee;

    /**
     * 是否绑定银行卡 0-否 1-是
     */
    @NotNull(message = "请选择是否绑定银行卡")
    private Integer isBank;


    /**
     * 持卡人姓名
     */
    private String bankUserName;
    /**
     * 身份证
     */
    private String idCard;
    /**
     * 银行卡号
     */
    private String bankNumber;
    /**
     * 银行名称
     */
    private String bankName;
    /**
     * 银行卡类型 1-储蓄卡 2-信用卡
     */
    private Integer bankCardType;
    /**
     * 银行编码
     */
    private String bankCode;
    /**
     * 银行预留手机号
     */
    private String bankPhone;

    /**
     * 备注
     */
    private String remark;

}

