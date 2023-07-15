package com.yuqian.itax.park.entity.vo;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Setter
@Getter
public class UsableParkBusinessScopeVO implements Serializable {
    private static final long serialVersionUID = -1L;

    /**
     * 园区id
     */
    private Long parkId;

    /**
     * 园区名称
     */
    private String parkName;

    /**
     * 行业及对应经营范围列表
     */
    private List<UsableParkIndustryVO> parkIndustryList;

    /**
     * 是否匹配园区 0-否 1-是
     */
    private Integer isSuited;

    /**
     * 税收分类编码匹配园区经营范围
     */
    private String taxCodeBusinessScope;

    /**
     * 增值税减免（分）
     */
    private Long vATBreaksAmount;

    /**
     * 所得税减免（分）
     */
    private Long incomeTaxBreaksAmount;

    /**
     * 核定说明
     */
    private String verifyDesc;

    /**
     * 园区政策说明
     */
    private String parkPolicyDesc;

    /**
     * 所属城市
     */
    private String parkCity;
}
