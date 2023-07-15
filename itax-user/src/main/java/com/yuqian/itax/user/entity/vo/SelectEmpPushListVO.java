package com.yuqian.itax.user.entity.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
public class SelectEmpPushListVO implements Serializable {

    private static final long serialVersionUID = -1L;

    /**
     * 员工ID
     */
    @ApiModelProperty(value = "员工ID")
    private Long id;

    /**
     * 员工名称（优先显示实名，未实名显示昵称）
     */
    @ApiModelProperty(value = "员工名称（优先显示实名，未实名显示昵称）")
    private String memberName;

    /**
     * 员工手机号
     */
    @ApiModelProperty(value = "员工手机号")
    private String memberPhone;

    /**
     * 直推用户数
     */
    @ApiModelProperty(value = "直推用户数")
    private Long pushMemberCount = 0L;

    /**
     * 直推用户列表
     */
    @ApiModelProperty(value = "直推用户列表")
    private List<EmployeePushListVO> pushList;

}
