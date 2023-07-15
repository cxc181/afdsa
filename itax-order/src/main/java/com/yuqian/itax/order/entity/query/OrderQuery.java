package com.yuqian.itax.order.entity.query;

import com.yuqian.itax.common.base.entity.query.BaseQuery;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

/**
 *  @Author: Kaven
 *  @Date: 2019/12/6 16:33
 *  @Description: 订单查询实体类
 */
@Getter
@Setter
public class OrderQuery extends BaseQuery implements Serializable {
    /**
     * 机构编码
     */
    @ApiModelProperty(value = "机构编码")
    private String oemCode;
    /**
     * 订单号（模糊查询）
     */
    @ApiModelProperty(value = "订单编号")
    private String likeOrderNo;
    /**
     * 会员手机号（模糊查询）
     */
    @ApiModelProperty(value = "注册账号")
    private String likeMemberPhone;
    /**
     * 用户昵称（模糊查询）
     */
    @ApiModelProperty(value = "用户名")
    private String likeMemberName;
    /**
     * 用户名
     */
    private String realName;
    /**
     * 开票企业（模糊查询）
     */
    private String likeCompName;

    /**
     * 支付方式 1-线上支付 2-线下支付
     */
    private String payType;
    /**
     * 会员等级 -1->员工 0-普通会员  1-税务顾问  2-城市服务商
     */
    @ApiModelProperty(value = "会员等级 -1-员工 0-普通会员  1-VIP 2-白银会员 3-税务顾问 4-铂金会员 5-城市服务商")
    private Integer memberLevel;
    /**
     * 企业类型 1-个体开户 2-个独开户 3-有限合伙 4-有限责任
     */
    @ApiModelProperty(value = "企业类型 1-个体开户 2-个独开户 3-有限合伙 4-有限责任")
    private Integer companyType;
    /**
     * 园区id
     */
    @ApiModelProperty(value = "园区id")
    private Long parkId;
    /**
     * 订单金额开始
     */
    @ApiModelProperty(value = "订单金额开始")
    private Long orderAmtBeg;
    /**
     * 订单金额结束
     */
    @ApiModelProperty(value = "订单金额结束")
    private Long orderAmtEnd;
    /**
     * 添加时间开始
     */
    @ApiModelProperty(value = "添加时间开始")
    private Date addTimeBeg;
    /**
     * 添加时间结束
     */
    @ApiModelProperty(value = "添加时间结束")
    private Date addTimeEnd;
    /**
     * 园区名称
     */
    @ApiModelProperty(value = "园区名称")
    private String parkName;
    /**
     * 订单状态   订单类型： 工商注册  0-待提交  1-审核中  2-待付款 3-待名称核准 4-待设立登记 5-待用户签名 6-待经办人签名 7-待领证 8-已完成
     开票： 0-待创建 1-待付款 2-待审核 3-出票中 4-待发货 5-出库中 6-待收货 7-已签收
     */
    @ApiModelProperty(value = "订单状态   订单类型： 工商注册  0-待提交  1-审核中  2-待付款 3-待名称核准 4-待设立登记 5-待用户签名 6-待经办人签名 7-待领证 8-已完成\n" +
            "     开票： 0-待创建 1-待付款 2-待审核 3-出票中 4-待发货 5-出库中 6-待收货 7-已签收")
    private Integer orderStatus;

    /**
     * 订单状态（多状态）   订单类型： 工商注册  0-待提交  1-审核中  2-待付款 3-待名称核准 4-待设立登记 5-待用户签名 6-待经办人签名 7-待领证 8-已完成
     开票： 0-待创建 1-待付款 2-待审核 3-出票中 4-待发货 5-出库中 6-待收货 7-已签收
     */
    private String orderStatuss;
    /**
     * 开票记录状态 0-待提交、1-人工出票、2-待补票、3-出票中断、4-出票失败、5-推送失败、6-待确认、7-已完成 8-已取消 9-出票中
     */
    @ApiModelProperty(value = "开票记录状态: 0-待提交、1-人工出票、2-待补票、3-出票中断、4-出票失败、5-推送失败、6-待确认、7-已完成 8-已取消 9-出票中")
    private Integer status;
    /**
     * 开票记录状态(多状态) 0-待提交、1-人工出票、2-待补票、3-出票中断、4-出票失败、5-推送失败、6-待确认、7-已完成 8-已取消 9-出票中
     */
    private String statuss;
    /**
     * 一级推广人账号
     */
   @ApiModelProperty(value = "一级推广人账号")
    private String likeAccountFirst;
    /**
     * 一级推广人等级-1->员工 0-普通会员  1-税务顾问  2-城市服务商
     */
    @ApiModelProperty(value = "一级推广人等级 -1->员工 0-普通会员  1-税务顾问  2-城市服务商")
    private Integer levelFirst;
    /**
     * 二级推广人账号
     */
    @ApiModelProperty(value = "二级推广人账号")
    private String likeAccountTwo;
    /**
     * 二级推广人等级
     */
    @ApiModelProperty(value = "二级推广人等级 -1->员工 0-普通会员  1-税务顾问  2-城市服务商")
    private Integer levelTwo;
    /**
     * 城市合伙人
     */
    @ApiModelProperty(value = "城市合伙人")
    private String likeCityProviders;
    /**
     * 高级城市合伙人
     */
    @ApiModelProperty(value = "高级城市合伙人")
    private String likeCityPartner;
    /**
     * 机构名称（模糊）
     */
    @ApiModelProperty(value = "机构名称")
    private String likeOemName;
    /**
     * 发票类型 1-普通发票 2-增值税发票
     */
    @ApiModelProperty(value = "发票类型 1-普通发票 2-增值税发票")
    private Integer invoiceType;
    /**
     * 开票金额起始
     */
    @ApiModelProperty(value = "开票金额起始")
    private Long invoiceAmtBeg;
    /**
     * 开票金额结束
     */
    @ApiModelProperty(value = "开票金额结束")
    private Long invoiceAmtEnd;
    /**
     * 开票金额最小值(分）
     */
    @ApiModelProperty(value = "开票金额最小值")
    private Long invoiceMountMin;

    /**
     * 开票金额最大值(分）
     */
    @ApiModelProperty(value = "开票金额最大值")
    private Long invoiceMountMax;
    /**
     * 抬头公司
     */
    @ApiModelProperty(value = "抬头公司")
    private String headCompanyName;
    /**
     * 开票记录编码
     */
    @ApiModelProperty(value = "开票记录编码")
    private String invoiceRecordNo;
    /**
     * 处理方式 1-线下、2-托管
     */
    @ApiModelProperty(value = "处理方式 1-线下、2-托管")
    private Integer handlingWay;
    /**
     * 集团开票订单编号
     */
    @ApiModelProperty(value = "集团开票订单编号")
    private String groupOrderNo;
    /**
     * 产品类型 1-个体开户 2-个独开户 3-有限合伙 4-有限责任 5-开票 6-税务顾问 7-城市服务商
     */
    @ApiModelProperty(value = "产品类型 1-个体开户 2-个独开户 3-有限合伙 4-有限责任 5-开票 6-税务顾问 7-城市服务商")
    private Integer prodType;

    /**
     * 查询的多个订单状态
     */
    private String orderStatuses;

    /**
     * 订单类型 1-充值 2-代理充值 3-提现 4-代理提现 5 - 工商开户 6-开票 7-用户升级
     */
    private Integer orderType;

    /**
     * 数据权限
     */
    private String tree;

    /**
     * 支付金额开始
     */
    @ApiModelProperty(value = "支付金额开始")
    private Long payAmtBeg;

    /**
     * 支付金额结束
     */
    @ApiModelProperty(value = "支付金额结束")
    private Long payAmtEnd;

    /**
     * 出票开始时间
     */
    @ApiModelProperty(value = "出票开始时间")
    private String ticketTimeStart;

    /**
     * 出票结束时间
     */
    @ApiModelProperty(value = "出票结束时间")
    private String ticketTimeEnd;
    /**
     * 经办人账号模糊
     */
    private String likeAgentAccount;

    /**
     * 流水状态
     */
    private Integer bankWaterStatus;
    /**
     * 开票审核通过开始时间
     */
    private String startAuditDate;

    /**
     * 开票审核结束通过时间
     */
    private String endAuditDate;
    /**
     * 开票完成开始时间
     */
    private String     startCompleteTimeDate;

    /**
     * 开票完成结束时间
     */
    private String     endCompleteTimeDate;

    /**
     * 经营者姓名
     */
    private String operatorName;

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
     * 购买会员等级 0-普通会员  1-VIP 2-白银会员 3-税务顾问 4-铂金会员 5-城市服务商
     */
    private Integer buyMemberLevel;
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
    /**
     * 渠道code
     */
    private String channelCode;
    /**
     * 推送状态：0-待推送 1-推送中 2-已推送 3-推送失败 4-无需推送
     */
    private Integer channelPushState;

    /**
     * 推送状态：用来一次查询多种状态
     */
    private String channelPushStates;
    /**
     * 开票方式 1-自助开票 2-集团代开 3-佣金开票(数据库)
     * 开票方式 1-自助开票 2-集团批量开票 3-佣金开票（返回页面）
     */
    private Integer createWay;

    /**
     * 是否已开启身份验证 0-未开启 1-已开启
     */
    private Integer isOpenAuthentication;
    /**
     * 发票方式 1-纸质发票 2-电子发票
     */
    private Integer invoiceWay;

    /**
     * 流水通过时间开始
     */
    private String flowTimeStart;
    /**
     * 流水通过时间结束
     */
    private String flowTimeEnd;

    /**
     * 用户身份
     */
    private String memberAuthType;

    /**
     * 成果状态 0-无需上传 1-成果前置 2-待上传 3-审核中 4-审核不通过 5-审核通过
     */
    private String achievementStatus;

    /**
     * 接入方名称
     */
    private String accessPartyName;

    /**
     * 开票类目名称
     */
    private String categoryName;

    /**
     * 作废时间开始
     */
    private String cancellationTimeStart;

    /**
     * 作废时间结束
     */
    private String cancellationTimeEnd;

    /**
     * 发票标识 0-正常 1-已作废/红冲 2-作废重开
     */
    private Integer invoiceMark;

    /**
     * 平台类型  1-平台 2-机构 3-园区 4-城市合伙人 5-城市合伙人
     */
    private Integer platformType;

    /**
     * 纳税人类型  1-小规模纳税人 2-一般纳税人
     */
    private Integer taxpayerType;

}