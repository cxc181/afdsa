package com.yuqian.itax.user.entity.query;

import com.yuqian.itax.common.base.entity.query.BaseQuery;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

/**
 *  @Author: Kaven
 *  @Date: 2019/12/18 20:48
 *  @Description: 城市服务商统计查询bean
 */
@Getter
@Setter
public class MemberExtendQuery extends BaseQuery implements Serializable {
    private static final long serialVersionUID = -1L;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 企业ID
     */
    private Long companyId;

    /**
     * 推广用户ID
     */
    private Long extendUserId;

    /**
     * 等级号：1-一级推广记录 2-二级推广记录
     */
    @ApiModelProperty(value = "等级号：1-一级推广记录 2-二级推广记录，默认查1级推广记录，当会员类型为城市服务商时必传")
    private Integer gradeNo;

    /**
     * 订单状态
     */
    @ApiModelProperty(value = "订单状态，默认查审核中的记录")
    private Integer orderStatus;

    /**
     * 状态名称
     */
    private String orderStatusName;

    /**
     * 订单类型 0-未注册企业 5-工商注册 6-开票 7-用户升级 8-工商注销 9-证件申请
     */
    @ApiModelProperty(value = "订单类型 0-未注册企业 5-企业注册 6-企业开票 7-会员升级 8-企业注销")
    private Integer orderType;

    /**
     * 会员类型：-1-员工 1-税务顾问会员 2-城市服务商会员
     */
    @ApiModelProperty(value = "会员类型：-1-员工 1-税务顾问 2-城市服务商")
    private Integer memberType;

    /**
     * 按月份查询
     */
    private String month;

    /**
     * 按日查询
     */
    private String day;

    /**
     * 推广角色：1-散客 2-直客 3-顶级直客
     */
    private Integer extendType;

    /**
     * 等级标识 -1-员工 0-普通会员 1-VIP会员 3-税务顾问会员 5-城市服务商会员
     */
    private Integer levelNo;
    /**
     * 会员名称
     */
    private String memberName;

    /**
     * 会员id
     */
    private Long memberId;

    /**
     * 渠道用户id列表
     */
    private List<Long> channelUserIds;
}