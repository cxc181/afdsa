package com.yuqian.itax.user.entity.vo;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
public class CompanyResoucesApplyRecordVO implements Serializable {

    private static final long serialVersionUID = -1L;


    private Long id;
    /**
     * 申请单编号
     */
    private String orderNo;
    /**
     * 申请类型  1-领用  2-归还
     */
    private String applyType;
    /**
     * 申请时间
     */
    private Date addTime;
    /**
     * 所属企业
     */
    private String CompanyName;
    /**
     * 企业类型 1-个体户 2-个人独资企业 3-有限合伙公司 4-有限责任公司
     */
    private Integer companyType;
    /**
     * 申请状态  状态  0-待付款 1-待发货  2-出库中 3-待签收 4-已签收 5-已取消
     */
    private Integer status;

    /**
     * 证件名称   1-公章 2-财务章 3-对公账号u盾 4-营业执照  5-营业执照副本 6-发票章 ，多个资源直接用 逗号分割
     */
    private String applyResouces;


    /**
     * 申请人账号
     */
    private String memberAccount;
    /**
     * 申请人姓名
     */
    private String memberName;

    /**
     * 所属园区
     */
    private String parkName;
    /**
     * 快递费（元）
     */
    private BigDecimal postageFees;
    /**
     * 所属OEM
     */
    private String oemName;
    /**
     * 所属OEM
     */
    private String oemCode;

}
