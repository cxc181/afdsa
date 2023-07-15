package com.yuqian.itax.user.entity.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @Author: yejian
 * @Date: 2020/11/12 14:49
 * @Description: 会员个人基本信息展示类
 */
@Getter
@Setter
public class MemberBaseInfoApiVO implements Serializable {
    private static final long serialVersionUID = -1L;

    /**
     * 会员id
     */
    @ApiModelProperty(value = "会员id")
    private Long userId;

    /**
     * 会员账号
     */
    @ApiModelProperty(value = "会员账号")
    private String memberAccount;

    /**
     * 会员昵称
     */
    @ApiModelProperty(value = "会员昵称")
    private String memberName;

    /**
     * 会员头像
     */
    @ApiModelProperty(value = "会员头像")
    private String headImg;

    /**
     * 会员等级
     */
    @ApiModelProperty(value = "会员等级")
    private Integer levelNo;

    /**
     * 会员等级名称
     */
    @ApiModelProperty(value = "会员等级名称")
    private String levelName;

    /**
     * 用户类型 1-会员 2-系统用户
     */
    @ApiModelProperty(value = "用户类型 1-会员 2-系统用户")
    private Integer userType;

    /**
     * 状态 0-禁用 1-正常 2-注销
     */
    @ApiModelProperty(value = "状态 0-禁用 1-正常 2-注销")
    private Integer status;

    /**
     * token
     */
    @ApiModelProperty(value = "token")
    private String token;
}
