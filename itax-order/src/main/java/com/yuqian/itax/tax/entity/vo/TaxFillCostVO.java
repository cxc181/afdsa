package com.yuqian.itax.tax.entity.vo;

import lombok.Getter;
import lombok.Setter;
import org.jeecgframework.poi.excel.annotation.Excel;

import java.io.Serializable;

@Setter
@Getter
public class TaxFillCostVO implements Serializable {

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
     * 是否0成本申报
     */
    @Excel(name = "是否0成本申报")
    private String isNoCost;

    /**
     * 成本项名称/成本项金额
     */
    @Excel(name = "成本项名称/成本项金额")
    private String costItems;

    /**
     * 失败原因
     */
    @Excel(name = "失败原因")
    private String reg;

    private boolean flag = true;
}
