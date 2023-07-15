package com.yuqian.itax.tax.entity.vo;

import lombok.Getter;
import lombok.Setter;
import org.jeecgframework.poi.excel.annotation.Excel;

import java.io.Serializable;
import java.math.BigDecimal;

@Setter
@Getter
public class TaxAuditVO implements Serializable {

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
     * 应退税费
     */
    @Excel(name = "应退税费")
    private String recoverableTaxMoney;

    /**
     * 审核结果
     */
    @Excel(name = "审核结果")
    private String auditResult;

    /**
     * 备注
     */
    @Excel(name = "备注")
    private String remark;

    /**
     * 失败原因
     */
    @Excel(name = "失败原因")
    private String reg;

    private boolean flag = true;
}
