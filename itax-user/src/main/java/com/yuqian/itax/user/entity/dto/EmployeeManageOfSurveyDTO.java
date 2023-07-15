package com.yuqian.itax.user.entity.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @ClassName: EmployeeManageOfResultDTO
 * @Description: 业绩概况查询条件
 * @Author: yejian
 * @Date: Created in 2020/6/5
 * @Version: 1.0
 */
@Getter
@Setter
public class EmployeeManageOfSurveyDTO implements Serializable {

    private static final long serialVersionUID = -1L;

    /**
     * 用户ID
     */
    @ApiModelProperty(value = "用户ID")
    private Long memberId;

    /**
     * 机构编码
     */
    @ApiModelProperty(value = "机构编码")
    private String oemCode;

    /**
     *  月份
     */
    @ApiModelProperty(value = "月份")
    private String month;

    /**
     *  开始时间
     */
    @ApiModelProperty(value = "开始时间")
    private String startDate;

    /**
     *  结束时间
     */
    @ApiModelProperty(value = "结束时间")
    private String endDate;

}
