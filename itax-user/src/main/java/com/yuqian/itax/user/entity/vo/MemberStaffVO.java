package com.yuqian.itax.user.entity.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class MemberStaffVO implements Serializable {

    private static final long serialVersionUID = -1L;

    /**
     * 主键id
     */
    @ApiModelProperty(value = "主键id")
    private Long id;

    /**
     * 会员账号
     */
    @ApiModelProperty(value = "会员账号")
    private String memberAccount;

    /**
     * 会员头像
     */
    @ApiModelProperty(value = "会员头像")
    private String headImg;

    /**
     * 会员手机号
     */
    @ApiModelProperty(value = "会员手机号")
    private String memberPhone;

    /**
     * 用户名
     */
    @ApiModelProperty(value = "用户名")
    private String realName;

    /**
     * 层级树
     */
    @ApiModelProperty(value = "层级树")
    private String memberTree;

    /**
     * 月开户数
     */
    @ApiModelProperty(value = "月开户数")
    private Long monthRegCount;
    /**
     * 月分润额
     */
    @ApiModelProperty(value = "月分润额")
    private Long monthProfitsCount;

    /**
     * 状态：1-正常 0-禁用 2-注销
     */
    @ApiModelProperty(value = "状态：1-正常 0-禁用 2-注销")
    private Integer status;

}
