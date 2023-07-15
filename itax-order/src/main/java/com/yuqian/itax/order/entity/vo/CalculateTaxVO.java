package com.yuqian.itax.order.entity.vo;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @Description 税费测算——计算税费VO
 * @Author  lmh
 * @Date   2022/09/26
 */
@Getter
@Setter
public class CalculateTaxVO implements Serializable {

    private static final long serialVersionUID = -1L;

    /**
     * 增值附加税税费（分）
     */
    private Long vatAndSurchargeTax;

    /**
     * 增值税税费（分）
     */
    private Long vatTax;

    /**
     * 增值税税率（%）
     */
    private BigDecimal vatRate;

    /**
     * 附加税税费（分）
     */
    private Long surchargeTax;

    /**
     * 城建税税费（分）
     */
    private Long urbanConstructionTax;

    /**
     * 城建税税率（%）
     */
    private BigDecimal urbanConstructionTaxRate;

    /**
     * 教育附加税税费（分）
     */
    private Long educationSurchargeTax;

    /**
     * 教育附加税税率（%）
     */
    private BigDecimal educationSurchargeTaxRate;

    /**
     * 地方教育附加税税费（分）
     */
    private Long localEducationSurcharge;

    /**
     * 地方教育附加税税率（%）
     */
    private BigDecimal localEducationSurchargeRate;

    /**
     * 所得税税费（分）
     */
    private Long incomeTax;

    /**
     * 所得税税率（%）
     */
    private BigDecimal incomeTaxRate;

    /**
     * 个人所得税税费（分）
     */
    private Long personageIncomeTax;

    /**
     * 个人所得税税率（%）
     */
    private BigDecimal personageIncomeTaxRate;

    /**
     * 企业所得税税费（分）
     */
    private Long companyIncomeTax;

    /**
     * 企业所得税税率（%）
     */
    private BigDecimal companyIncomeTaxRate;

    /**
     * 默认园区id
     */
    private Long defaultParkId;

    /**
     * 默认园区名称
     */
    private String defaultParkName;

    /**
     * 总税费（分）
     */
    private Long allTax;
}
