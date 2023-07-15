package com.yuqian.itax.park.entity.vo;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Setter
@Getter
public class IndustryPolicyVO implements Serializable {

    /**
     * 税费政策主键
     */
    private Long policyId;

    /**
     * 税种类型 1-所得税 2-增值税 3-附加税
     */
    private Integer taxType;

    /**
     * 行业id
     */
    private Long industryId;

    /**
     * 适用行业(行业名称)
     */
    private String industryName;

    /**
     * 增值税个税减免周期 1-按月 2-按季度
     */
    private Integer vatBreaksCycle;

    /**
     * 税率
     */
    private List<TaxRatesRulesVO> rules;
}
