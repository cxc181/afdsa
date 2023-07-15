package com.yuqian.itax.user.entity.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
public class EmployeeListVO implements Serializable {

    private static final long serialVersionUID = -1L;

    /**
     * 是否城市服务商自己 0-是 1-否
     */
    @ApiModelProperty(value = "是否城市服务商自己 0-是 1-否")
    private Integer isSelf;

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
     * 状态 1-正常 0-禁用 2-注销
     */
    @ApiModelProperty(value = "状态 1-正常 0-禁用 2-注销")
    private Integer status;

    /**
     * 会员手机号
     */
    @ApiModelProperty(value = "会员手机号")
    private String memberPhone;

    /**
     * 添加时间
     */
    @ApiModelProperty(value = "添加时间")
    private Date addTime;

    /**
     * 直推用户数
     */
    @ApiModelProperty(value = "直推用户数")
    private Long pushMemberCount = 0L;

    /**
     * 裂变用户数
     */
    @ApiModelProperty(value = "裂变用户数")
    private Long fissionMemberCount = 0L;

    /**
     * 总个体数
     */
    @ApiModelProperty(value = "总个体数")
    private Long personalityCount = 0L;

    /**
     * 累计分润费
     */
    @ApiModelProperty(value = "累计分润费")
    private Long totalProfit = 0L;

}
