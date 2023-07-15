package com.yuqian.itax.user.entity.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
public class MemberVO implements Serializable {
    private static final long serialVersionUID = -1L;

    /**
     * 主键id
     */
    private Long id;
    /**
     * 会员账号
     */
    private String memberAccount;
    /**
     * 会员昵称
     */
    private String memberName;
    /**
     * 用户名
     */
    private String realName;
    /**
     * 会员等级
     */
    private String levelName;
    /**
     * 注册时间
     */
    private Date addTime;
    /**
     * 一级推广人手机号码
     */
    private String phoneFirst;
    /**
     * 二级推广人手机号码
     */
    private String phoneTwo;
    /**
     * 城市合伙人名称
     */
    private String cityProvidersName;
    /**
     * 高级城市合伙人名称
     */
    private String cityPartnerName;
    /**
     * 机构名称
     */
    private String oemName;
    /**
     * 账户状态  1-正常 0-禁用 2-注销
     */
    private String status;

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
     * 订单号
     */
    @ApiModelProperty(value = "订单号")
    private String orderNo;
}
