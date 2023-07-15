package com.yuqian.itax.agent.entity.vo;

import com.yuqian.itax.agent.entity.OemInvoiceCategoryRelaEntity;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Setter
@Getter
public class InvoiceInfoByOemVO implements Serializable {

    private static final long serialVersionUID = -1L;

    /**
     * 主键id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 机构编码
     */
    private String oemCode;

    /**
     * 机构编码
     */
    private String oemName;

    /**
     * 公司名称
     */
    private String companyName;

    /**
     * 公司地址
     */
    private String companyAddress;

    /**
     * 税号
     */
    private String ein;

    /**
     * 电话号码
     */
    private String phone;

    /**
     * 注册地址
     */
    private String registAddress;

    /**
     * 开户银行
     */
    private String bankName;

    /**
     * 银行账号
     */
    private String bankNumber;

    /**
     * 收件人
     */
    private String recipient;

    /**
     * 联系电话
     */
    private String recipientPhone;

    /**
     * 详细地址
     */
    private String recipientAddress;

    /**
     * 状态  1-可用 0-不可用
     */
    private Integer status;

    /**
     * 省编码
     */
    private String provinceCode;

    /**
     * 省名称
     */
    private String provinceName;

    /**
     * 市编码
     */
    private String cityCode;

    /**
     * 市名称
     */
    private String cityName;

    /**
     * 区编码
     */
    private String districtCode;

    /**
     * 区名称
     */
    private String districtName;

    /**
     * 开票类目
     */
    private List<OemInvoiceCategoryRelaEntity> categoryList;
    /**
     * 收票邮箱
     */
    private String email;

    /**
     * 托管状态 0-未托管 1-已托管
     */
    private Integer hostingStatus;

    /**
     * 税务盘类型 1-ukey 2-税控盘
     */
    private Integer taxDiscType;

    /**
     * 税务盘编号
     */
    private String taxDiscCode;

    /**
     * 票面金额类型 1-1w 2-10w 3-100w
     */
    private Integer faceAmountType;

    /**
     * 票面金额(分)
     */
    private Long faceAmount;

    /**
     * 通道方 1-百旺
     */
    private Integer channel;

    /**
     * 是否立即开票 0-否 1-是
     */
    private Integer isImmediatelyInvoice;
}
