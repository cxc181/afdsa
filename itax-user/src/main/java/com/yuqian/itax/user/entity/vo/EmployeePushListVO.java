package com.yuqian.itax.user.entity.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class EmployeePushListVO implements Serializable {

    private static final long serialVersionUID = -1L;

    /**
     * 会员id
     */
    @ApiModelProperty(value = "会员id")
    private Long id;

    /**
     * 会员名称（优先显示实名，未实名显示昵称）
     */
    @ApiModelProperty(value = "会员名称（优先显示实名，未实名显示昵称）")
    private String memberName;

    /**
     * 会员手机号
     */
    @ApiModelProperty(value = "会员手机号")
    private String memberPhone;

}
