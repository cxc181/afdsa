package com.yuqian.itax.user.entity.vo;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
public class MemberPageInfoVO implements Serializable {
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
     * 所在省市
     */
    private String areaName;
    /**
     * 会员等级
     */
    private String levelName;

    /**
     * 等级标识 -1-员工 0-普通会员  1-VIP 2-白银会员 3-税务顾问 4-铂金会员 5城市服务商
     */
    private Integer levelNo;
    /**
     * 会员等级编码
     */
    private Integer memberLevel;
    /**
     * 注册时间
     */
    private Date addTime;

     //一级推广人手机号码
    private String oneAccount;

     //二级推广人手机号码
    private String twoAccount;

    //城市合伙人名称
    private String cityProvidersName;

     // 高级城市合伙人名称
    private String cityPartnerName;
    /**
     * 机构名称
     */
    private String oemName;
    /**
     * 账户状态  1-正常 0-禁用 2-注销
     */
    private Integer status;
    /**
     * 产品邀请人
     */
    private String parentMemberId;
    /**
     * 邀请人账号
     */
    private String parentMemberAccount;
    /**
     * 渠道名称
     */
    private String channelName;

     //城市服务商员工上限
    private Integer employeesLimit;

     //推广人会员等级名称
    private String levelOneName;


    //推广类型 1-散客 2-直客 3-顶级直客
    private Integer extendType;

    //所属员工手机号
    private String attributionEmployeesAccount;


    //上级城市服务商手机号(所属城市服务商手机号)
    private String upDiamondAccount;

     //上上级城市服务商账号(上上级城市服务商手机号)
    private String superDiamondAccount;

    /**
     * 人群标签名称
     */
    private String crowdLabelName;

    /**
     * 接入方名称
     */
    private String accessPartyName;

    /**
     * 用户类型
     */
    private String memberAuthType;

    /**
     * 实名推送状态推送状态：0-待推送 1-推送中 2-已推送 3-推送失败 4-无需推送
     */
    private String authPushState;

}
