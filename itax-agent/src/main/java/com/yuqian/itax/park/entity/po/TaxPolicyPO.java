package com.yuqian.itax.park.entity.po;

import com.yuqian.itax.park.entity.TaxRulesConfigEntity;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Setter
@Getter
public class TaxPolicyPO implements Serializable {

    /**
     * 税率政策ID
     */
    private  Long id;

    /**
     * 企业类型 1-个体工商 2-个体独资 3-有限责任 4-有限合伙
     */
    @NotNull(message="请输入企业类型")
    @Min(value = 1, message = "企业类型错误")
    @Max(value = 4, message = "企业类型错误")
    private Integer companyType;

    /**
     * 增值税个税减免周期 1-按月 2-按季度
     */
    @NotNull(message="增值税申报周期不能为空")
    @Min(value = 1, message = "增值税申报周期错误")
    @Max(value = 4, message = "增值税申报周期型错误")
    private  Integer vatBreaksCycle;
    /**
     *增值税个税减免额度
     */
    @NotNull(message="请输入增值税减免额度")
    @Min(value = 0, message = "增值税减免额度只支持大于0的数字")
    @Max(value = Long.MAX_VALUE, message = "增值税减免额度已超过最大限制")
    private Long vatBreaksAmount;

    /**
     * 附加税减免额度
     */
    @NotNull(message="请输入附加税减免额度")
    @Min(value = 0, message = "附加税减免额度只大于0的数字")
    @Max(value = Long.MAX_VALUE, message = "附加税减免额度已超过最大限制")
    private Long surchargeBreaksAmount;

    /**
     * 附加税减免周期 1-按月 2-按季度
     */
    private Integer surchargeBreaksCycle;

    /**
     * 所得税减免额度
     */
    @NotNull(message="请输入所得税减免额度")
    @Min(value = 0, message = "所得税减免额度只支持大于0的数字")
    @Max(value = Long.MAX_VALUE, message = "所得税减免额度已超过最大限制")
    private Long incomeTaxBreaksAmount;

    /**
     * 所得税减免周期 1-按月 2-按季度
     */
    @NotNull(message="所得税申报周期不能为空")
    @Min(value = 1, message = "所得税申报周期错误")
    @Max(value = 4, message = "所得税申报周期型错误")
    private Integer incomeTaxBreaksCycle;

    /**
     * 税费规则配置List
     */
    List<TaxRulesConfigEntity> taxRulesConfigEntityList;

    /**
     * 计税方式（1：预缴征收率，2：核定应税所得率）
     */
    private Integer levyWay;

    /**
     * 所得税征收方式 1-查账征收 2-核定征收
     */
    @NotNull(message="请输入所得税征收方式")
    @Min(value = 1, message = "所得税征收方式错误")
    @Max(value = 2, message = "所得税征收方式错误")
    private Integer incomeLevyType;

    /**
     * 纳税人类型  1-小规模纳税人 2-一般纳税人
     */
//    @NotNull(message="请输入纳税人类型")
//    @Min(value = 1, message = "纳税人类型错误")
//    @Max(value = 2, message = "纳税人类型错误")
    private Integer taxpayerType;

    /**
     * 印花税申报周期 1-按月 2-按季度
     */
    private Integer stampDutyBreaksCycle;

    /**
     * 印花税是否减半 0-否 1-是
     */
    private Integer isStampDutyHalved;

    /**
     * 水利建设基金申报周期 1-按月 2-按季度
     */
    private Integer waterConservancyFundBreaksCycle;

    /**
     * 水利建设基金是否减半 0-否 1-是
     */
    private Integer isWaterConservancyFundHalved;

    /**
     * 应纳税所得额减免(分)
     */
    private Long incomeTaxableIncomeBreaks;

    /**
     * 所得税税收减免比例
     */
    private BigDecimal incomeTaxReliefRatio;
}
