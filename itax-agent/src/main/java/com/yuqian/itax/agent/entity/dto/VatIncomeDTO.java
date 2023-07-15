package com.yuqian.itax.agent.entity.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 *  @Author: Kaven
 *  @Date: 2020/3/27 10:36
 *  @Description: 增值税所得税接收bean
 */
@Setter
@Getter
public class VatIncomeDTO implements Serializable {

    private static final long serialVersionUID = -1L;

    /**
     * 计算类型 1-按月 2-按季
     */
    @NotNull(message = "计算类型不能为空")
    private  Integer calType;

    /**
     * 企业类型 1-个体工商 2-个体独资 3-有限责任 4-有限合伙
     */
    @NotNull(message = "企业类型不能为空")
    private  Integer companyType;

    /**
     * 发票类型：1->普通发票；2-增值税发票(增值税发票无减免)
     */
    private Integer invoiceType;

    /**
     * 园区ID
     */
    @NotNull(message = "园区ID不能为空")
    private  Long parkId;

    /**
     * 第一季度预计开票金额
     */
    private Long firstSeasonAmount;

    /**
     * 第二季度预计开票金额
     */
    private Long secondSeasonAmount;

    /**
     * 第三季度预计开票金额
     */
    private Long thirdSeasonAmount;

    /**
     * 第四季度预计开票金额
     */
    private Long fourthSeasonAmount;

    /**
     * 月均开票金额
     */
    private Long monthAvgAmount;

    /**
     * 第1月份预计开票金额
     */
    private Long firstMonthAmount;

    /**
     * 第2月份预计开票金额
     */
    private Long secondMonthAmount;

    /**
     * 第3月份预计开票金额
     */
    private Long thirdMonthAmount;

    /**
     * 第4月份预计开票金额
     */
    private Long fourthMonthAmount;

    /**
     * 第5月份预计开票金额
     */
    private Long fifthMonthAmount;

    /**
     * 第6月份预计开票金额
     */
    private Long sixthMonthAmount;

    /**
     * 第7月份预计开票金额
     */
    private Long seventhMonthAmount;

    /**
     * 第8月份预计开票金额
     */
    private Long eighthMonthAmount;

    /**
     * 第9月份预计开票金额
     */
    private Long ninthMonthAmount;

    /**
     * 第10月份预计开票金额
     */
    private Long tenthMonthAmount;

    /**
     * 第11月份预计开票金额
     */
    private Long eleventhMonthAmount;

    /**
     * 第12月份计开票金额
     */
    private Long twelvethMonthAmount;
}
