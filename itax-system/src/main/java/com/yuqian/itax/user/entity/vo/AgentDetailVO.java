package com.yuqian.itax.user.entity.vo;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;

@Setter
@Getter
public class AgentDetailVO implements Serializable {
    private static final long serialVersionUID = -1L;

    /**
     * 主键id
     */
    private Long id;

    /**
     * 机构名称
     */
    private String oemName;
    /**
     * 机构编码
     */
    private String oemCode;
    /**
     * 代理商名称
     */
    private String nickname;
    /**
     * 账账号类型  1-管理员  2-坐席客服 3-普通用户
     */
    private Integer accountType;
    /**
     * 平台类型  1-平台 2-机构 3-园区 4-城市合伙人 5-城市合伙人
     */
    private Integer platformType;
    /**
     * 账号
     */
    private String username;
    /**
     * 手机号
     */
    private String phone;

    /**
     * 代理城市
     */
    private String cityName;
    /**
     * 代理省
     */
    private String provinceName;
    /**
     * 关联推广账号
     */
    private String bindingAccount;

    /**
     * 会费分润率
     */
    private BigDecimal membershipFee;
    /**
     * 开户分润率
     */
    private BigDecimal registerFee;
    /**
     * 开票分润率
     */
    private BigDecimal invoiceFee;
    /**
     * 企业注销服务费分润率
     */
    private BigDecimal cancelCompanyFee;


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
