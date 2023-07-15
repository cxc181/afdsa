package com.yuqian.itax.order.entity.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 税费明细VO
 * @Date: 2021/3/18
 */
@Getter
@Setter
public class TaxFeeDetailVO implements Serializable {

	private static final long serialVersionUID = -1L;

    /**
     * 本季（月）开票金额
     */
    @ApiModelProperty(value = "本季（月）开票金额")
    private Long periodInvoiceAmount;

    /**
     * 本次应缴增值税
     */
    @ApiModelProperty(value = "本次应缴增值税")
    private Long payableVatFee;

    /**
     * 增值税税率
     */
    @ApiModelProperty(value = "增值税税率")
    private BigDecimal vatFeeRate;

    /**
     * 本季（月）已缴增值税
     */
    @ApiModelProperty(value = "本季（月）已缴增值税")
    private Long paidVatFee;

    /**
     * 本次应缴附加税
     */
    @ApiModelProperty(value = "本次应缴附加税")
    private Long payableSurcharge;

    /**
     * 附加税税率
     */
    @ApiModelProperty(value = "附加税税率")
    private BigDecimal surchargeRate;

    /**
     * 本季（月）已缴附加税
     */
    @ApiModelProperty(value = "本季（月）已缴附加税")
    private Long paidSurcharge;

    /**
     * 本次应缴所得税
     */
    @ApiModelProperty(value = "本次应缴所得税")
    private Long payableIncomeTax;

    /**
     * 所得税税率
     */
    @ApiModelProperty(value = "所得税税率")
    private BigDecimal incomeTaxRate;

    /**
     * 本季（月）已缴所得税
     */
    @ApiModelProperty(value = "本季（月）已缴所得税")
    private Long paidIncomeTax;

    /**
     * 应税所得率
     */
    @ApiModelProperty(value = "应税所得率")
    private BigDecimal taxableIncomeRate;

    /**
     * 本年已缴所得税
     */
    @ApiModelProperty(value = "本年已缴所得税")
    private Long paidIncomeTaxYear;

    /**
     * 计税方式（1：预缴征收率，2：核定应税所得率）
     */
    @ApiModelProperty(value = "计税方式（1：预缴征收率，2：核定应税所得率）")
    private Integer levyWay;

    /**
     * 本周期累计应缴增值税
     */
    @ApiModelProperty(value = "本周期累计应缴增值税")
    private Long periodPayableVatFee;

    /**
     * 本周期累计应缴专票增值税
     */
    @ApiModelProperty(value = "本周期累计应缴专票增值税")
    private Long periodPayableSpecialVatFee;

    /**
     * 印花税税率
     */
    private BigDecimal stampDutyRate;

    /**
     * 应缴印花税金额
     */
    private Long stampDutyAmount;

    /**
     * 水利建设基金税率
     */
    private BigDecimal waterConservancyFundRate;

    /**
     * 应缴水利建设基金金额
     */
    private Long waterConservancyFundAmount;

    /**
     * 纳税人类型 1-小规模纳税人 2-一般纳税人
     */
    private Integer taxpayerType;

    /**
     * 增值税申报周期 1-按月 2-按季度
     */
    private Integer vATBreaksCycle;

    /**
     * 附加税申报周期 1-按月 2-按季度
     */
    private Integer surchargeBreaksCycle;

    /**
     * 印花税申报周期 1-按月 2-按季度
     */
    private Integer stampDutyBreaksCycle;

    /**
     * 水利建设基金申报周期 1-按月 2-按季度
     */
    private Integer waterConservancyFundBreaksCycle;

    /**
     * 所得税申报周期 1-按月 2-按季度
     */
    private Integer incomeTaxBreaksCycle;

    /**
     * 所得税征收方式 1-查账征收 2-核定征收
     */
    private Integer incomeLevyType;
}
