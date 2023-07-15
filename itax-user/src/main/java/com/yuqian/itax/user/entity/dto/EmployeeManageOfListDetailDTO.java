package com.yuqian.itax.user.entity.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @ClassName: EmployeeManageOfResultDTO
 * @Description: 员工业绩明细查询条件
 * @Author: yejian
 * @Date: Created in 2020/6/6
 * @Version: 1.0
 */
@Getter
@Setter
public class EmployeeManageOfListDetailDTO implements Serializable {

    private static final long serialVersionUID = -1L;

    /**
     * 员工ID
     */
    @ApiModelProperty(value = "员工ID")
    private Long empId;

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
