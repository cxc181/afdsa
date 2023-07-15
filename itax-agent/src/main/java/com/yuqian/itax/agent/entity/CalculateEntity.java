package com.yuqian.itax.agent.entity;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 *  @Author: yejian
 *  @Date: 2020/3/27 15:36
 *  @Description: 增值税所得税计算bean
 */
@Setter
@Getter
public class CalculateEntity implements Serializable {

    private static final long serialVersionUID = -1L;

    /**
     * 计算类型 1-按季 2-按月
     */
    private  Integer calType;

    /**
     * 企业类型 1-个体工商 2-个体独资 3-有限责任 4-有限合伙
     */
    private  Integer companyType;

    /**
     * 发票类型：1->普通发票；2-增值税发票(增值税发票无减免)
     */
    private Integer invoiceType;

    /**
     * 园区ID
     */
    private  Long parkId;

    /**
     * 季开票金额
     */
    private Long seasonAmount;

    /**
     * 月开票金额
     */
    private Long monthAmount;

    /**
     * 历史应纳税所得额
     */
    private Long historyTaxableIncome;

    /**
     * 年度已缴所得税税费
     */
    private Long historyTaxes;

}
