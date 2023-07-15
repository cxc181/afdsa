package com.yuqian.itax.user.entity.query;

import com.yuqian.itax.common.base.entity.query.BaseQuery;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

/**
 *  @Author: yejian
 *  @Date: 2020/06/09 10:19
 *  @Description: 员工直推列表查询实体类
 */
@Getter
@Setter
public class EmployeeManageOfPushListQuery extends BaseQuery {

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
     * 员工ID
     */
    @ApiModelProperty(value = "员工ID")
    private Long empId;

    /**
     * 下拉选择用户ID
     */
    @ApiModelProperty(value = "下拉选择用户ID")
    private Long selectUserId;

}
