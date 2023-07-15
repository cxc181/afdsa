package com.yuqian.itax.agent.entity.vo;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Setter
@Getter
public class OemDetailVO implements Serializable {
    private static final long serialVersionUID = -1L;

    /**
     * 机构账号
     */
    private String username;

    /**
     * 机构名称
     */
    private String oemName;
    /**
     * 机构编码
     */
    private String oemCode;
    /**
     * 公司名称
     */
    private String companyName;

    /**
     * 绑定手机号
     */
    private String phone;

    /**
     * logo短
     */
    private String oemLogo;
    /**
     * logo
     */
    private String dLogo;
    /**
     * 添加园区
     */
    private List<Long> parkIdList;

    /**
     * 客服热线
     */
    private String customerServiceTel;

    /**
     * 是否绑定银行卡 0-否 1-是
     */
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

    /**
     * 公司地址
     */
    private String belongsCompanyAddress;

    /**
     * 统一社会信用代码
     */
    private String ein;

    /**
     * 运营网址
     */
    private String netAddress;

    /**
     *  是否接入国金助手 1-接入 0-不接入
     */
    private Integer isOpenChannel;

    /**
     *  国金助手地址
     */
    private String channelUrl;

    /**
     *  国金加密参数
     */
    private String secKey;
    /**
     * 是否大客户 0-否 1-是
     */
    private Integer isBigCustomer;

    /**
     * 收费标准协议模板id
     */
    private Long agreementTemplateId;

    /**
     * 模板名称
     */
    private String templateName;

    /**
     * 公章图片地址
     */
    private String officialSealImg;

    /**
     * 公章地址图片
     */
    private String httpOfficialSealImg;

    /**
     * 是否发送查账征收税单消息   0-不发送  1-发送
     */
    private Integer isSendAuditBillsMessage;

    /**
     * 收单oem机构名称
     */
    private String otherPayOemName;

    /**
     * 收单机构oemcode
     */
    private String otherPayOemcode;

    /**
     * 是否其他oem机构收单  0-否 1-是
     */
    private Integer isOtherOemPay;

    /**
     * 是否收银台 0-否 1-是
     */
    private Integer isCheckstand;

    /**
     * oem小程序appid
     */
    private String oemAppid;

    /**
     * 收款账号开户行
     */
    private String receivingBankAccountBranch;

    /**
     * 收款银行账号
     */
    private String receivingBankAccount;

}
