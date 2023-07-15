package com.yuqian.itax.park.entity.vo;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
public class TaxPolicySelectVO implements Serializable {
    private static final long serialVersionUID = -1L;


    /**
     * 主键id
     */
    private Long id;

    /**
     * 企业类型 1-个体工商 2-个体独资 3-有限责任 4-有限合伙
     */
    private Integer companyType;

    /**
     * 年度开票总额
     */
    private Long totalInvoiceAmount;

    /**
     * 季开票额度
     */
    private Long quarterInvoiceAmount;

    /**
     * 政策文件链接
     */
    private String policyFileUrl;
}
