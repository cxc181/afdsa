package com.yuqian.itax.order.entity.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @Description 税费测算——计算税费DTO
 * @Author  lmh
 * @Date   2022/09/26
 */
@Getter
@Setter
public class CalculateTaxDTO implements Serializable {

    private static final long serialVersionUID = -1L;

    /**
     * 企业类型 2-个人独资企业 3-有限合伙 4-有限责任
     */
    @NotNull(message = "企业类型不能为空")
    private Integer companyType;

    /**
     * 纳税人类型 1-小规模纳税人 2-一般纳税人
     */
    private Integer taxpayerType;

    /**
     * 增值税税率（%）
     */
    @NotNull(message = "增值税率为空")
    private BigDecimal vatRate;

    /**
     * 总收入(含税)（分）
     */
    @NotNull(message = "总收入(含税)为空")
    private Long allEarning;

    /**
     * 总成本(含税)（分）
     */
    @NotNull(message = "总成本(含税)为空")
    private Long allCosting;

    /**
     * 专票收入（分）
     */
    private Long specialInvoiceEarning;

    /**
     * 普票收入（分）
     */
    private Long plainInvoiceEarning;

    /**
     * 分红收入（分）
     */
    private Long dividendEarning;

    /**
     * 经营收入（分）
     */
    private Long manageEarning;

    /**
     * 专票成本(税率6%)（分）
     */
    private Long sixPCSpecialInvCosting;

    /**
     * 专票成本(税率9%)（分）
     */
    private Long ninePCSpecialInvCosting;

    /**
     * 专票成本(税率13%)（分）
     */
    private Long thirteenPCSpecialInvCosting;

    /**
     * 普票成本（分）
     */
    private Long plainInvoiceCosting;

    /**
     * 无票支出(人力成本) （分）
     */
    private Long noInvoiceCosting;

    /**
     * 股权占比(自然人) (%)
     */
    private BigDecimal personageEquityRatio;

    /**
     * 股权占比(企业) （%）
     */
    private BigDecimal companyEquityRatio;

    /**
     * 默认园区id
     */
    private Long parkId;

    /**
     * 机构编码
     */
    private String oemCode;
}
