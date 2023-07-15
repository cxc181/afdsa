package com.yuqian.itax.user.entity.query;

import com.yuqian.itax.common.base.entity.query.BaseQuery;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 *  @Author: yejian
 *  @Date: 2020/06/06 10:19
 *  @Description: 员工列表查询实体类
 */
@Getter
@Setter
public class EmployeeManageOfListQuery extends BaseQuery {

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
     * 查询条件（姓名或手机号模糊查询）
     */
    @ApiModelProperty(value = "查询条件（姓名或手机号模糊查询）")
    private String nameOrPhone;

}
