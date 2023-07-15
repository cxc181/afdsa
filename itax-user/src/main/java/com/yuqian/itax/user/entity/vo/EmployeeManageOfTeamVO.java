package com.yuqian.itax.user.entity.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class EmployeeManageOfTeamVO implements Serializable {

    private static final long serialVersionUID = -1L;

    /**
     * 当前员工数
     */
    @ApiModelProperty(value = "当前员工数")
    private Long employeeCount = 0L;

    /**
     * 邀请员工上限
     */
    @ApiModelProperty(value = "邀请员工上限")
    private Integer employeesLimit;

    /**
     * 本月分润
     */
    @ApiModelProperty(value = "本月分润")
    private Long monthProfit = 0L;

    /**
     * 累计分润
     */
    @ApiModelProperty(value = "累计分润")
    private Long totalProfit = 0L;

    /**
     * 总直推用户数
     */
    @ApiModelProperty(value = "总直推用户数")
    private Long totalPushMember = 0L;

    /**
     * 总裂变用户数
     */
    @ApiModelProperty(value = "总裂变用户数")
    private Long totalFissionMember = 0L;

    /**
     * 总个体
     */
    @ApiModelProperty(value = "总个体数")
    private Long totalPersonality = 0L;

}
