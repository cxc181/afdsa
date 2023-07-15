package com.yuqian.itax.park.entity.dto;

import com.yuqian.itax.park.entity.vo.TaxRatesRulesVO;
import com.yuqian.itax.util.validator.Add;
import com.yuqian.itax.util.validator.Update;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Setter
@Getter
public class IndustryPolicyDTO implements Serializable {

    /**
     * 1：新增，2：编辑
     */
    private int type;

    /**
     * 行业类型id
     */
    @NotNull(message="请选择适用行业")
    private Long industryId;

    /**
     * 税费政策主键
     */
    @NotNull(message="税费政策主键不能为空")
    private Long policyId;

    /**
     * 税费规则配置List
     */
    List<TaxRatesRulesVO> rules;

    /**
     * 变更行业id
     */
    private Long changeIndustryId;

    public void setIndustryId(Long industryId) {
        this.industryId = industryId;
        this.changeIndustryId = industryId;
    }
}
