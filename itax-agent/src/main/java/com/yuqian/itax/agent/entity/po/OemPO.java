package com.yuqian.itax.agent.entity.po;

import com.yuqian.itax.agent.entity.dto.OemParkIndustryAdminDTO;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Setter
@Getter
public class OemPO implements Serializable {

    private static final long serialVersionUID = -1L;

    private  Long id;

    /**
     * 账号
     */
    @NotBlank(message = "请输入机构账号")
    @Size(min = 6, max = 20, message = "账号不能小于6位字符，超过20位字符")
    @Pattern(regexp = "^[a-zA-Z0-9_]{8,16}$", message = "请输入8~16位，支持字母/数字/下划线组合")
    private String username;

    /**
     * 机构名称
     */
    @NotBlank(message = "请输入机构名称")
    private String oemName;
    /**
     * 机构编码
     */
    @NotBlank(message = "请输入机构编码")
    private String oemCode;

    /**
     * 公司名称
     */
    @NotBlank(message = "请输入公司名称")
    private String companyName;
    /**
     *运营平台网址
     */
    @NotBlank(message = "请输运营平台网址")
    private String netAddress;
    /**
     * 绑定手机
     */
    @NotBlank(message = "请输入绑定手机")
    private String phone;

    /**
     * logo
     */
    @NotBlank(message = "请上传公司Logo")
    private String oemLogo;
    /**
     *客服电话
     */
    @NotBlank(message = "请输入客服电话")
    private  String customerServiceTel;


    /**
     *  是否绑卡0-否  1-是
     */
    @NotNull(message = "请选择是否绑卡")
    private Integer isbank;
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
     * 新增园区ID
     */
    @NotNull(message = "请选择新增园区")
    private List<OemParkIndustryAdminDTO> parkIdList;

    /**
     *备注
     */
    private  String remark;

    /**
     * 机构状态 0-下架1-上架 2-暂停
     */
    private Integer status;
    /**
     * 结算周期
     */
    private Integer settlementCycle;
    /**
     * 结算类型 1-按周 2-按月
     */
    private Integer settlementType;
    /**
     * 会费分润率
     */
    private BigDecimal membershipFee;
    /**
     * 开户分润率
     */
    private BigDecimal registerfee;
    /**
     * 开票分润率
     */
    private BigDecimal invoicefee;

    /**
     * 企业注销服务费分润率
     */
    private BigDecimal cancelCompanyFee;

    /**
     * 公司地址
     */
    @NotBlank(message = "请输入公司地址")
    @Size(max = 128, message = "公司地址不能超过128个字")
    private String belongsCompanyAddress;

    /**
     * 统一社会信用代码
     */
    @NotBlank(message = "请输入统一社会信用代码")
    @Size(max = 32, message = "统一社会信用代码不能超过32个字")
    private String ein;

    /**
     *  是否接入国金助手 1-接入 0-不接入
     */
    @NotNull(message="是否接入国金助手")
    @Min(value = 0, message = "是否接入国金助手参数错误")
    @Max(value = 1, message = "是否接入国金助手参数错误")
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
     *  是否大客户 0-否 1-是
     */
    @NotNull(message = "请选择是否大客户")
    private Integer isBigCustomer;

    /**
     * 协议模板id
     */
    private Long agreementTemplateId;

    /**
     * 公章图片地址
     */
    private String officialSealImg;

    /**
     * 公章图片地址（公域）
     */
    private String officialSealImgPublic;

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
    @NotNull(message = "是否收银台不能为空")
    @Min(value = 0, message = "是否收银台参数错误")
    @Max(value = 1, message = "是否收银台参数错误")
    private Integer isCheckstand;

    /**
     * oem小程序appid
     */
    private String oemAppid;

    /**
     * 收款银行账号
     */
    @Size(max = 30, message = "收款银行账号不能超过30个字")
    private String receivingBankAccount;

    /**
     * 收款账号开户行
     */
    @Size(max = 50, message = "收款账号开户行不能超过50个字")
    private String receivingBankAccountBranch;

}
