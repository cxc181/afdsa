package com.yuqian.itax.user.entity.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class EmployeeManageOfSurveyVO implements Serializable {

    private static final long serialVersionUID = -1L;

    /**
     * 新增直推用户数
     */
    @ApiModelProperty(value = "新增直推用户数")
    private Long pushMemberCount = 0L;

    /**
     * 新增直推个体数
     */
    @ApiModelProperty(value = "新增直推个体数")
    private Long pushPersonalityCount = 0L;

    /**
     * 新增直推分润费
     */
    @ApiModelProperty(value = "新增直推分润费")
    private Long pushProfit = 0L;

    /**
     * 新增裂变用户数
     */
    @ApiModelProperty(value = "新增裂变用户数")
    private Long fissionMemberCount = 0L;

    /**
     * 新增裂变个体数
     */
    @ApiModelProperty(value = "新增裂变个体数")
    private Long fissionPersonalityCount = 0L;

    /**
     * 新增裂变分润费
     */
    @ApiModelProperty(value = "新增裂变分润费")
    private Long fissProfit = 0L;

}