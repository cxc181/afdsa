package com.yuqian.itax.user.entity.po;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Setter
@Getter
public class CorporateAccountApplyOrderPO implements Serializable {

    private static final long serialVersionUID = -1L;

    /**
     *  公户申请单id
     */
    private Long id;

    /**
     * 开户行id(对公户配置id)
     */
    @NotNull(message="请选择开户行")
    private Long corporateAccountConfigId;

    /**
     * 银行账户
     */
    @NotBlank(message="请输入银行账户")
    private String corporateAccount;

    /**
     * 制单员编号(专线)
     */
    @NotBlank(message="请输入制单员编号(专线)")
    private String voucherMemberCode;

    /**
     * 委托项目编号(专线)
     */
    @NotBlank(message="请输入委托项目编号(专线)")
    private String entrustProjectCode;

    /**
     * 项目用途编号(专线)
     */
    @NotBlank(message="请输入项目用途编号(专线)")
    private String projectUseCode;

    /**
     * 委托项目编号(网金)
     */
    @NotBlank(message="请输入委托项目编号(网金)")
    private String entrustProjectCodeWj;

    /**
     * 项目用途编号(网金)
     */
    @NotBlank(message="请输入项目用途编号(网金)")
    private String projectUseCodeWj;

    /**
     * 持卡人姓名
     */
    @NotBlank(message="请输入持卡人姓名")
    private String bankUserName;
    /**
     * 持卡人身份证号
     */
    @NotBlank(message="请输入持卡人身份证号")
    private String idCard;

    /**
     * 银行卡号
     */
    @NotBlank(message="请输入银行卡号")
    private String bindBankCardNumber;
    /**
     * 预留手机号
     */
    @NotBlank(message="请输入预留手机号")
    private String bindBankPhone;

    private Long companyId;

    private String companyName;

    private Long memberId;

    private String memberPhone;

    private String oemCode;

    /**
     * 提现限额单笔(分)
     */
    @NotNull(message="提现限额单笔(分)")
    @Min(value = 100,message = "单笔最小提现限额不能小于1元")
    @Max(value = 500000000,message = "单笔最大提现限额不能大于500万元")
    private Long singleWithdrawalLimit;
    /**
     * 提现限额单日(分)
     */
    @NotNull(message="提现限额单日(分)")
    @Min(value = 100,message = "单日最小提现限额不能小于1元")
    @Max(value = 500000000,message = "单日最大提现限额不能大于500万元")
    private Long dailyWithdrawalLimit;
}
