package com.yuqian.itax.user.entity.po;


import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class AgentUpdatePO  implements Serializable {
    private static final long serialVersionUID = -1L;

    /**
     * id
     */
    @NotNull(message = "请选择修改得代理商")
    private  Long id;
    /**
     * 机构代码
     */
    @NotBlank(message = "请选择正确得所属机构")
    private String oemCode;
    /**
     * 绑定手机号
     */
    @NotBlank(message = "请输入代理商绑定手机号")
    private String phone;


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

