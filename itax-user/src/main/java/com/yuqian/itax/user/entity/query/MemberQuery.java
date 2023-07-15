package com.yuqian.itax.user.entity.query;

import com.yuqian.itax.common.base.entity.query.BaseQuery;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemberQuery extends BaseQuery {
    private static final long serialVersionUID = -1L;
    /**
     * 注册账号
     */
    private String memberAccount;
    /**
     * 用户名
     */
    private String realName;
    /**
     * 所在省
     */
    private String provinceName;
    /**
     * 所在市
     */
    private String cityName;
    /**
     * 会员等级
     */
    private String memberLevel;

    /**
     * 产品邀请人
     */
    private String parentMemberId;

    /**
     * 渠道名称
     */
    private String channelName;

    /**
     * 渠道名称
     */
    private String channelCode;

    /**
     * 一级推广人手机号码
     */
    private String phoneFirst;
    /**
     * 所属城市合伙人
     */
    private String cityProvidersName;
    /**
     * 所属高级城市合伙人
     */
    private String cityPartnerName;
    /**
     * 二级推广人手机号码
     */
    private String phoneTwo;
    /**
     * 账户状态  1-正常 0-禁用 2-注销 -1全部
     */
    private String status;
    /**
     * 所属OEM
     */
    private String oemName;
    /**
     * 所属OEM
     */
    private String oemCode;
    /**
     * 当前登陆账号用户树
     */
    private  String tree;

//    /**
//     * 推广角色 1-散客 2-直客 3-顶级直客
//     */
//    private Integer extendType;

    /**
     * 推广人会员等级 -1 - 员工 0-普通会员  1-VIP 2-白银会员 3-税务顾问 4-铂金会员 5-城市服务商
     */
    private Integer levelOne;

    /**
     * 所属员工手机号
     */
    private String attributionEmployeesAccount;

    /**
     * 上级城市服务商手机号(所属城市服务商手机号)
     */
    private String upDiamondAccount;

    /**
     * 上上级城市服务商账号(所属上上级城市服务商手机号)
     */
    private String superDiamondAccount;
    /**
     * 会员标签 0-普通 1-海星
     */
    private Integer sign;
    /**
     * 邀请人账号
     */
    private String parentMemberAccount;

    /**
     * 用户身份 1-个人 2-企业
     */
    private String memberAuthType;

    /**
     * 实名推送状态推送状态：0-待推送 1-推送中 2-已推送 3-推送失败 4-无需推送
     */
    private String authPushState;

    /**
     * 人群标签名称
     */
    private String crowdLabelName;

    /**
     * 接入方名称
     */
    private String accessPartyName;
}
