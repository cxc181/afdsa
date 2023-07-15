package com.yuqian.itax.order.entity.query;

import com.yuqian.itax.common.base.entity.query.BaseQuery;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

/**
 * 企业续费订单查询实体类
 * @author：pengwei
 * @Date：2021/2/4 9:53
 * @version：1.0
 */
@Getter
@Setter
public class ContRegisterOrderQuery extends BaseQuery implements Serializable {

    /**
     * 订单编号（模糊查询）
     */
    private String likeOrderNo;

    /**
     * 注册账号（模糊查询）
     */
    private String likeMemberPhone;

    /**
     * 企业名称（模糊查询）
     */
    private String likeCompName;

    /**
     * 城市合伙人
     */
    private String likeCityProviders;

    /**
     * 高级城市合伙人
     */
    private String likeCityPartner;

    /**
     * 机构编码
     */
    private String oemCode;

    /**
     * 推广人账号
     */
    private String likeAccountFirst;

    /**
     * 推广人等级 -1->员工 0-普通会员 1-VIP 3-税务顾问 5-城市服务商
     */
    private Integer levelFirst;

    /**
     * 所属员工手机号
     */
    private String attributionEmployeesAccount;

    /**
     * 上级城市服务商手机号(所属城市服务商手机号)
     */
    private String upDiamondAccount;

    /**
     * 上上级城市服务商账号(上上级城市服务商手机号)
     */
    private String superDiamondAccount;
    /**
     * 产品邀请人
     */
    private String parentMemberId;
    /**
     * 渠道名称
     */
    private String channelName;
    /**
     * 渠道编码
     */
    private String channelCode;
    /**
     * 推送状态：0-待推送 1-推送中 2-已推送 3-推送失败 4-无需推送
     */
    private Integer channelPushState;

    /**
     * 邀请人账号
     */
    private String parentMemberAccount;

    /**
     * 园区id
     */
    private Long parkId;

    /**
     * 会员等级 -1->员工 0-普通会员 1-VIP 3-税务顾问 5-城市服务商
     */
    private Integer memberLevel;

    /**
     * 订单状态 0-待支付 1-支付中 2-已完成 3-已取消
     */
    private Integer orderStatus;

    /**
     * 添加时间开始
     */
    private Date addTimeBeg;
    /**
     * 添加时间结束
     */
    private Date addTimeEnd;

    /**
     * 订单类型 1-充值 2-代理充值 3-提现 4-代理提现 5 - 工商注册 6-开票 7-用户升级 8-工商注销 9-证件申请 10-对公户申请 11-对公户提现 12-消费开票 13-补税 14-退税 15-托管费续费
     */
    private Integer orderType;

    /**
     * 数据权限
     */
    private String tree;

    /**
     * 用户身份
     */
    private String memberAuthType;

    /**
     * 平台类型  1-平台 2-机构 3-园区 4-城市合伙人 5-城市合伙人
     */
    private Integer platformType;

    /**
     * 企业类型1-个体工商户 2-个人独资企业 3-有限合伙 4-有限责任
     */
    private Integer companyType;

}