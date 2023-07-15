package com.yuqian.itax.tax.entity.vo;

import lombok.Getter;
import lombok.Setter;
import org.jeecgframework.poi.excel.annotation.Excel;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 查账征收
 */
@Setter
@Getter
public class DownloadCompanyTaxBillByAccountsVO implements Serializable {

    private static final long serialVersionUID = -1L;

    /**
     * 税款所属期
     */
    @Excel(name = "税款所属期")
    private String taxBillTime;
    /**
     *园区名称
     */
    @Excel(name = "所属园区")
    private String parkName;
    /**
     * 公司名称
     */
    @Excel(name = "企业名称")
    private String companyName;
    /**
     * 税号
     */
    @Excel(name = "税号")
    private String ein;
    /**
     * 经营者名称
     */
    @Excel(name = "经营者名称")
    private String operatorName;

    /**
     * 本期开票金额
     */
    @Excel(name = "本季开票金额")
    private BigDecimal invoiceMoney;

//    /**
//     * 增值扣除税费
//     */
//    @Excel(name = "增值扣除税费（本季）")
//    private BigDecimal vatDeductionTaxfee;
//
//    /**
//     * 附加税扣除税费
//     */
//    @Excel(name = "附加税扣除税费（本季）")
//    private BigDecimal additionalDeductionTaxfee;

    /**
     * 个税扣除金额（分）
     */
    @Excel(name = "个税扣除金额（本季）")
    private BigDecimal iitDeductionAmount;

    /**
     * 本季累计成本金额
     */
    @Excel(name = "本季累计成本")
    private BigDecimal quarterCostAmount;

    /**
     * 年度累计收入金额
     */
    @Excel(name = "本年不含税收入")
    private BigDecimal yearIncomeAmount;

    /**
     * 本年累计成本
     */
    @Excel(name = "本年累计成本")
    private BigDecimal yearCostAmount;

    /**
     * 总税费
     */
    @Excel(name = "应缴税费")
    private BigDecimal shouldTaxMoney;

    /**
     * 增值税
     */
    @Excel(name = "增值税")
    private BigDecimal vatShouldTaxMoney;
    /**
     * 附加税
     */
    @Excel(name = "附加税")
    private BigDecimal additionalShouldTaxMoney;
    /**
     * 所得税
     */
    @Excel(name = "所得税")
    private BigDecimal incomeShouldTaxMoney;

    /**
     * 冻结税额
     */
    @Excel(name = "冻结税额")
    private BigDecimal incomeTaxYearFreezeAmount;

    /**
     * 本年个税扣除金额
     */
    @Excel(name = "本年个税扣除金额")
    private BigDecimal yearIitDeductionAmount;
    /**
     * 个税可退税额
     */
    @Excel(name = "个税可退税额")
    private BigDecimal incomeTaxRefundAmount;

    /**
     * 税单状态
     */
    @Excel(name = "税单状态", replace = { "-_null","待确认_0","待退税_1","待补税_2","正常_3","已退税_4","已补税_5","待核对_6","待填报成本_7","待申报_8","已作废_9","待财务审核_10","审核不通过_11" }, height = 10, width = 22)
    private Integer taxBillStatus;

    /**
     * 生成方式 1季度自动生成 2企业注销生成
     */
    @Excel(name = "税单类型", replace = { "-_null","季度自动生成_1","企业注销生成_2" }, height = 10, width = 22)
    private Integer generateType;
}
