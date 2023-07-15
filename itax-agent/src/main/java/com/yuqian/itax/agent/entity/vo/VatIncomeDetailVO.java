package com.yuqian.itax.agent.entity.vo;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 *  @Author: Kaven
 *  @Date: 2020/3/27 10:37
 *  @Description: 所得税明细返回实体类
 */
@Setter
@Getter
public class VatIncomeDetailVO implements Serializable {

    private static final long serialVersionUID = -1L;

    /**
     * 月份/季度序号
     */
    private Integer index;

    /**
     * 适用税率
     */
    private BigDecimal rate;

    /**
     * 预计开票金额
     */
    private Long amount;

    /**
     * 应缴所得税
     */
    private BigDecimal incomeTax;

    /**
     * 发票类型：1->普通发票；2-增值税发票(增值税发票无减免)
     */
    private Integer invoiceType;
}
