package com.yuqian.itax.user.entity.vo;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.math.BigDecimal;


@Setter
@Getter
public class CompanyCorporateAccountVO implements Serializable {

    private static final long serialVersionUID = -1L;

    private Long id;

    private String memberAccount;

    private String memberName;

    private String companyName;

    private Long corporateAccountConfigId;//公户开户行ID

    private String corporateAccountBankName;

    private String corporateAccount;

    private String operatorName;

    private String operatorTel;

    private String idCardNumber;

    private String addTime;

    private Integer status;

    private String oemName;

    private String oemCode;
    /**
     * 预留手机号
     */
    private String bindBankPhone;
    /**
     * 银行卡号
     */
    private String bindBankCardNumber;

    /**
     * 制单员编号(专线)
     */
    private String voucherMemberCode;

    /**
     * 委托项目编号(专线)
     */
    private String entrustProjectCode;

    /**
     * 项目用途编号(专线)
     */
    private String projectUseCode;

    /**
     * 委托项目编号(网金)
     */
    private String entrustProjectCodeWj;

    /**
     * 项目用途编号(网金)
     */
    private String projectUseCodeWj;
    /**
     * 账户余额
     */
    private BigDecimal balanceMoney=new BigDecimal(0);

    private Long parkId;
    /**
     * 园区名称
     */
    private String parkName;

    /**
     * 银行总部名称
     */
    private String headquartersName;

    /**
     * 银行总部编号
     */
    private String headquartersNo;

    /**
     * 年费状态
     */
    private Integer overdueStatus;

    /**
     * 个体户状态
     */
    private Integer companyStatus;

    /**
     * 提现限额单笔(分)
     */
    private Long singleWithdrawalLimit;
    /**
     * 提现限额单日(分)
     */
    private Long dailyWithdrawalLimit;

    public void setCorporateAccount(String corporateAccount){
        if (StringUtils.isNotBlank(corporateAccount)) {
            this.corporateAccount = StringUtils.overlay(corporateAccount, "****", 4, corporateAccount.length() - 4);
        }
    }

    public void setIdCardNumber(String idCardNumber){
        if (StringUtils.isNotBlank(idCardNumber)) {
            this.idCardNumber = StringUtils.overlay(idCardNumber, "****", 4, idCardNumber.length() - 4);
        }
    }

}
