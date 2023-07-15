package com.yuqian.itax.system.entity.vo;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 园区行业介绍VO
 */
@Setter
@Getter
public class ParkIndustryPresentationVO implements Serializable {

    private static final long serialVersionUID = 5878882028698006259L;

    /**
     * 经营范围
     */
    private String businessContent;

    /**
     * 税费种认定
     */
    private String taxName;

    /**
     * 开票类目
     */
    private List<String> categoryNames;

    /**
     * 增值税减免
     */
    private BigDecimal vatBreaksAmount;

    /**
     * 所得税减免
     */
    private BigDecimal incomeTaxBreaksAmount;

    /**
     * 园区名称
     */
    private String parkName;

    /**
     * 增值税个税减免周期 1-按月 2-按季度
     */
    private Integer vatBreaksCycle;

    /**
     * 所得税减免周期 1-按月 2-按季度
     */
    private Integer incomeTaxBreaksCycle;

    /**
     * 园区政策说明
     */
    private String parkPolicyDesc;
    /**
     *  企业类型1-个体工商户 2-个人独资企业 3-有限合伙 4-有限责任
     */
    private Integer companyType;
}
