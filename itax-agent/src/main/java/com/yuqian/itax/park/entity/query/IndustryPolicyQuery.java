package com.yuqian.itax.park.entity.query;

import com.yuqian.itax.common.base.entity.query.BaseQuery;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Setter
@Getter
public class IndustryPolicyQuery extends BaseQuery {

    /**
     * 行业id
     */
    private Long industryId;

    /**
     * 适用行业(行业名称)
     */
    private String industryName;

    /**
     * 税费政策主键
     */
    @NotNull(message="税费政策主键不能为空")
    private Long policyId;

    /**
     * 税种类型 1-所得税 2-增值税 3-附加税
     */
    private Integer taxType;

    /**
     * 企业类型 1-个体工商 2-个体独资 3-有限合伙 4-有限责任
     */
    @NotNull(message="企业类型不能为空")
    private Integer companyType;
}
