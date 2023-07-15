package com.yuqian.itax.tax.entity.vo;


import lombok.Getter;
import lombok.Setter;
import org.jeecgframework.poi.excel.annotation.Excel;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Setter
@Getter
public class DownloadCompanyTaxBillVOAdmin implements Serializable {

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

}
