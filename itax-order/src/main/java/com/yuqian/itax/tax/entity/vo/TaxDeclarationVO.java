package com.yuqian.itax.tax.entity.vo;

import lombok.Getter;
import lombok.Setter;
import org.jeecgframework.poi.excel.annotation.Excel;

import java.io.Serializable;
import java.math.BigDecimal;

@Setter
@Getter
public class TaxDeclarationVO implements Serializable {

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
     * 无票收入金额
     */
    @Excel(name = "无票收入金额")
    private BigDecimal withoutTicketAmount;

    /**
     * 本季应缴增值税
     */
    @Excel(name = "本季应缴增值税")
    private BigDecimal vatShouldTaxMoney;

    /**
     * 本季应缴附加税
     */
    @Excel(name = "本季应缴附加税")
    private BigDecimal additionalShouldTaxMoney;

    /**
     * 本年应缴所得税
     */
    @Excel(name = "本年应缴所得税")
    private BigDecimal incomeShouldTaxMoney;

    /**
     * 个税可退税额
     */
    @Excel(name = "个税可退税额")
    private BigDecimal incomeTaxRefundAmount;

    /**
     * 备注
     */
    @Excel(name = "备注")
    private String remake;

    /**
     * 失败原因
     */
    @Excel(name = "失败原因")
    private String reg;

    private boolean flag = true;
}
