package com.yuqian.itax.agreement.entity.vo;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 协议模板
 * @Date: 2022年2月14日 14:35:19
 */
@Getter
@Setter
public class AgreementTemplateVO implements Serializable {

    private static final long serialVersionUID = -1L;

    /**
     * 用户姓名
     */
    private String realName = "用户姓名";

    /**
     * 用户身份证号
     */
    private String idCardNo = "用户身份证号";

    /**
     * 用户身份证地址
     */
    private String idCardAddr = "用户身份证地址";

    /**
     * 用户身份证照正面
     */
    private String idCardFront = "用户身份证照正面";

    /**
     * 用户身份证照反面
     */
    private String idCardBack = "用户身份证照反面";

    /**
     * 用户性别
     */
    private String userSex = "用户性别";

    /**
     * 用户手机号
     */
    private String memberPhone = "用户手机号";

    /**
     * 法人姓名
     */
    private String operatorName = "法人姓名";

    /**
     * 法人身份证号
     */
    private String legalIdCardNumber = "法人身份证号";

    /**
     *法人身份证地址
     */
    private String legalIdCardAddr = "法人身份证地址";

    /**
     * 法人身份证照正面
     */
    private String legalIdCardFront = "法人身份证照正面";

    /**
     * 法人身份证照反面
     */
    private String legalIdCardReverse = "法人身份证照反面";

    /**
     * 法人手机号
     */
    private String contactPhone = "法人手机号";

    /**
     * 法人性别
     */
    private String legalSex = "法人性别";

    /**
     * 个体户名称
     */
    private String registeredName = "个体户名称";

    /**
     * 备选字号一
     */
    private String shopNameOne = "备选字号一";

    /**
     * 备选字号二
     */
    private String shopNameTwo = "备选字号二";

    /**
     * 经营范围
     */
    private String businessScope = "经营范围";

    /**
     * 园区名称
     */
    private String parkName = "园区名称";

    /**
     * 园区所在地
     */
    private String parkCity = "园区所在地";

    /**
     * 园区详细地址
     */
    private String parkAddress = "园区详细地址";

    /**
     * 园区所属企业名称
     */
    private String parkBelongsCompanyName = "园区所属企业名称";

    /**
     * 园区所属企业地址
     */
    private String parkBelongsCompanyAddress = "园区所属企业地址";

    /**
     * 园区所属企业税号
     */
    private String parkEin = "园区所属企业税号";

    /**
     * 园区联系人
     */
    private String recipient = "园区联系人";

    /**
     * 园区联系人手机号
     */
    private String recipientPhone = "园区联系人手机号";

    /**
     * OEM机所属构企业名称
     */
    private String oemCompanyName = "OEM机所属构企业名称";

    /**
     * oem机构名称
     */
    private String oemName = "oem机构名称";

    /**
     * OEM机构所属企业税号
     */
    private String oemEin = "OEM机构所属企业税号";

    /**
     * OEM机构所属企业地址
      */
    private String oemBelongsCompanyAddress = "OEM机构所属企业地址";

    /**
     * OEM机构所属企业对公户账号
     */
    private String oemBankNumber = "OEM机构所属企业对公户账号";

    /**
     * OEM机构所属企业开户行
     */
    private String oemBankName = "OEM机构所属企业开户行";

    /**
     * 累计达到多少金额免注销费
     */
    private String cancelTotalLimit = "累计达到多少金额免注销费";

    /**
     * 注销费
     */
    private String prodAmount = "注销费";

    /**
     * 经营地址
     */
    private String businessAddress = "经营地址";

    /**
     * 经营者签名
     */
    private String signImg = "经营者签名";

    /**
     * 签名日期
     */
    private String signTime = "签名日期";

    /**
     * 托管费有效期
     */
    private String endTime = "托管费有效期";

    /**
     * 托管费
     */
    private String annualFee = "托管费";

    /**
     * 委托开始日期 自XX至YY，XX为用户签名日期，YY为签名日期往后推一年
     */
    private String  agentStartTime="委托开始日期";

    /**
     * 委托截止日期 自XX至YY，XX为用户签名日期，YY为签名日期往后推一年
     */
    private String  agentEndTime="委托截止日期";

    /**
     * 经办人姓名
     */
    private String  agentName="经办人姓名";

    /**
     * 经办人理人联系电话
     */
    private String  agentAccount="经办人联系电话";

    /**
     * 经办人身份证号
     */
    private String  agentIdCardNo="经办人身份证号";

    /**
     * 经办人身份证照正面
     */
    private String  agentIdCardFront="经办人身份证照正面";

    /**
     * 经办人身份证照反面
     */
    private String  agentIdCardBack="经办人身份证照反面";

    /**
     * 园区公章图片地址
     */
    private String parkOfficialSealImg ="园区公章";

    /**
     * oem机构公章图片地址
     */
    private String oemOfficialSealImg ="oem机构公章";
}
