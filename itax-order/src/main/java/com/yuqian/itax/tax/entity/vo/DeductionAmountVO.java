package com.yuqian.itax.tax.entity.vo;

import lombok.Getter;
import lombok.Setter;
import org.jeecgframework.poi.excel.annotation.Excel;

import java.io.Serializable;
import java.math.BigDecimal;

@Setter
@Getter
public class DeductionAmountVO implements Serializable {

    private static final long serialVersionUID = -1L;

    /**
     * 企业名称
     */
    @Excel(name = "企业名称")
    private String companyName;

    /**
     * 税号
     */
    @Excel(name = "税号")
    private String ein;

    /**
     * 应缴增值税
     */
    @Excel(name = "应缴增值税")
    private BigDecimal vatShouldTaxMoney;

    /**
     * 应缴附加税
     */
    @Excel(name = "应缴附加税")
    private BigDecimal additionalShouldTaxMoney;

    /**
     * 个税扣除金额
     */
    @Excel(name = "个税扣除金额")
    private BigDecimal iitDeductionAmount;

    /**
     * 失败原因
     */
    @Excel(name = "失败原因")
    private String reg;

    private boolean flag = true;
}
