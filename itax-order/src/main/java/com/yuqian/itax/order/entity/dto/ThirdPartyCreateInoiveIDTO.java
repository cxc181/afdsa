package com.yuqian.itax.order.entity.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.math.BigDecimal;

@Setter
@Getter
public class ThirdPartyCreateInoiveIDTO implements Serializable {
    private static final long serialVersionUID = -1L;

    /**
     * 机构编码
     */
    private String oemCode;
    /**
     * 接入方编码
     */
    private String accessPartyCode;
    /**
     * 企业id
     */
    private Long companyId;
    /**
     * 用户id
     */
    private Long userId;
    /**
     * 开票金额
     */
    private Long amount;
    /**
     * 增值税率
     */
    private BigDecimal vatRate;
    /**
     * 发票类型1、纸质发票；2、电子发票
     */
    private Integer invoiceWay;
    /**
     * 开票类型1、增值税普通发票；2、增值税专用发票
     */
    private Integer invoiceType;
    /**
     * 开票类目
     */
    private String categoryName;
    /**
     * 抬头公司名称
     */
    private String companyName;
    /**
     * 抬头公司地址
     */
    private String companyAddress;
    /**
     * 抬头税号
     */
    private String ein;
    /**
     * 银行卡号
     */
    private String bankNumber;

    /**
     * 开户行
     */
    private String bankName;
    /**
     * 抬头电话
     */
    private String phone;
    /**
     * 发票抬头收件人
     */
    private String recipient;
    /**
     * 发票抬头联系电话
     */
    private String recipientPhone;

    /**
     * 发票抬头详细地址
     */
    private String recipientAddress;
    /**
     * 发票抬头省编码
     */
//    private String provinceCode;

    /**
     * 发票抬头省名称
     */
    private String provinceName;

    /**
     * 发票抬头市编码
     */
//    private String cityCode;

    /**
     * 发票抬头市名称
     */
    private String cityName;

    /**
     * 发票抬头区编码
     */
//    private String districtCode;

    /**
     * 发票抬头区名称
     */
    private String districtName;

    /**
     * 邮箱
     */
    @Email(message = "邮箱格式不正确")
    private String email;
    /**
     * 发票备注
     */
    private String invoiceRemark;
    /**
     * 订单备注
     */
    private String remark;
    /**
     * 操作人
     */
    private String account;

    /**
     * 商品明细列表
     */
    private String goodsDetails;

    /**
     * 业务来源单号
     */
    @NotBlank(message = "业务来源单号不能为空")
    private String externalOrderNo;
}
