package com.yuqian.itax.agreement.entity.vo;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class AgreementPreviewVO implements Serializable {

    private static final long serialVersionUID = -1L;

    /**
     * 园区名称
     */
    private String parkName;

    /**
     * 园区所在地
     */
    private String parkCity;

    /**
     * 园区详细地址
     */
    private String parkAddress;

    /**
     * 园区所属企业名称
     */
    private String parkBelongsCompanyName;

    /**
     * 园区所属企业地址
     */
    private String parkBelongsCompanyAddress;

    /**
     * 园区所属企业税号
     */
    private String parkEin;

    /**
     * 园区联系人
     */
    private String recipient;

    /**
     * 园区联系人手机号
     */
    private String recipientPhone;

    /**
     * OEM机所属构企业名称
     */
    private String oemCompanyName;

    /**
     * OEM机构所属企业税号
     */
    private String oemEin;

    /**
     * OEM机构所属企业地址
     */
    private String oemBelongsCompanyAddress;

    /**
     * OEM机构所属企业对公户账号
     */
    private String oemBankNumber;

    /**
     * OEM机构所属企业开户行
     */
    private String oemBankName;

    /**
     * oem机构名称
     */
    private String oemName;

    /**
     * 累计达到多少金额免注销费
     */
    private String cancelTotalLimit;

    /**
     * 注销费
     */
    private String prodAmount;

    /**
     * 托管费
     */
    private String annualFee;

}
