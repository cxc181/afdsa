package com.yuqian.itax.user.entity.vo;

import com.github.pagehelper.PageInfo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 *  @Author: Kaven
 *  @Date: 2019/12/18 10:14
 *  @Description: 税务顾问统计信息展示类
 */
@Getter
@Setter
public class MemberExtendVO implements Serializable {
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
     * 用户名
     */
    @ApiModelProperty(value = "用户名")
    private String realName;

    /**
     * 会员等级
     */
    @ApiModelProperty(value = "会员等级")
    private String levelName;

    /**
     * 注册时间
     */
    @ApiModelProperty(value = "注册时间")
    private Date addTime;

    /**
     * 机构名称
     */
    @ApiModelProperty(value = "机构名称")
    private String oemName;

    /**
     * 账户状态  1-正常 0-禁用 2-注销
     */
    @ApiModelProperty(value = "账户状态 1-正常 0-禁用 2-注销")
    private String status;

    /**
     * 推广用户数
     */
    @ApiModelProperty(value = "（一级）推广用户数")
    private Long extendUserCount;

    /**
     * 城市服务商/员工二级用户数
     */
    @ApiModelProperty(value = "二级用户数")
    private Long extendSecondUserCount;

    /**
     * 累积开户(一级推广个体)数
     */
    @ApiModelProperty(value = "累积开户数")
    private Integer totalCount;

    /**
     * 城市服务商/员工二级个体数
     */
    @ApiModelProperty(value = "二级个体数")
    private Long secondCount;

    /**
     * 累积佣金（总佣金）
     */
    @ApiModelProperty(value = "累积佣金")
    private String totalAmount;

    /**
     * 待电子签名
     */
    @ApiModelProperty(value = "待电子签名")
    private Integer toBeSignCount;

    /**
     * 待视频认证
     */
    @ApiModelProperty(value = "待视频认证")
    private String toBeVideoCount;

    /**
     * 审核中
     */
    @ApiModelProperty(value = "审核中")
    private Integer toBeSureCount;

    /**
     * 待付款
     */
    @ApiModelProperty(value = "待付款")
    private String toPayCount;

    /**
     * 待出证
     */
    @ApiModelProperty(value = "待出证")
    private Integer toCheckoutCount;

    /**
     * 已完成
     */
    @ApiModelProperty(value = "已完成")
    private String finishedCount;

    /**
     * 政策奖励百分比
     */
    @ApiModelProperty(value = "政策奖励百分比")
    private BigDecimal membershipRate;

    /**
     * 分润比例
     */
    @ApiModelProperty(value = "分润比例")
    private BigDecimal profitsRate;

    /**
     * 下级推广用户列表
     */
    @ApiModelProperty(value = "下级推广用户列表")
    private PageInfo<MemberVO> memberPageList;
}
