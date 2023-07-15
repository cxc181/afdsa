package com.yuqian.itax.group.entity.vo;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.Date;

@Setter
@Getter
public class InvoiceHeadGroupVO implements Serializable {

    private static final long serialVersionUID = -1L;

    /**
     * 主键id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     *
     */
    private String oemCode;

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
     * 添加时间
     */
    private Date addTime;

    /**
     * 添加人
     */
    private String addUser;

    /**
     * 修改时间
     */
    private Date updateTime;

    /**
     * 修改人
     */
    private String updateUser;

    /**
     * 备注
     */
    private String remark;
}
