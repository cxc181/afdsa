package com.yuqian.itax.order.entity.vo;

import com.yuqian.itax.agent.entity.OemEntity;
import com.yuqian.itax.order.entity.MemberOrderRelaEntity;
import com.yuqian.itax.order.entity.OrderEntity;
import com.yuqian.itax.user.entity.MemberAccountEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.jeecgframework.poi.excel.annotation.Excel;

import java.util.Date;

/**
 *  @Author: Kaven
 *  @Date: 2019/12/6 16:34
 *  @Description: 订单前端展示实体类
 */
@Setter
@Getter
public class OrderVO {
    /**
     * 订单号
     */
    @ApiModelProperty(value = "订单号")
    private String orderNo;

    /**
     * 会员手机号
     */
    @ApiModelProperty(value = "会员手机号")
    private String memberPhone;

    /**
     * 用户昵称
     */
    @ApiModelProperty(value = "用户昵称")
    private String memberName;

    /**
     * 会员等级 -1->员工 0-普通会员  1-税务顾问  2-城市服务商
     */
    @ApiModelProperty(value = "会员等级 -1->员工 0-普通会员  1-税务顾问  2-城市服务商")
    private Integer memberLevel;

    /**
     * 企业类型 1-个体开户 2-个独开户 3-有限合伙 4-有限责任
     */
    @ApiModelProperty(value = "企业类型 1-个体开户 2-个独开户 3-有限合伙 4-有限责任")
    private Integer companyType;

    /**
     * 园区名称
     */
    @ApiModelProperty(value = "园区名称")
    private String parkName;

    /**
     * 订单金额
     */
    @ApiModelProperty(value = "订单金额")
    private Long orderAmount;

    /**
     * 优惠金额
     */
    @ApiModelProperty(value = "优惠金额")
    private Long discountAmount;

    /**
     * 支付金额
     */
    @ApiModelProperty(value = "支付金额")
    private Long payAmount;

    /**
     * 经办人账号
     */
    @ApiModelProperty(value = "经办人账号")
    private String agentAccount;

    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间")
    private Date addTime;

    /**
     订单状态   订单类型： 工商注册  0-待提交  1-审核中  2-待付款 3-待名称核准 4-待设立登记
     5-待用户签名 6-待经办人签名 7-待领证 8-已完成
     开票： 0-待创建 1-待付款 2-待审核 3-出票中 4-待发货 5-出库中 6-待收货 7-已签收
     */
    @ApiModelProperty(value = "订单状态   订单类型： 工商注册  0-待提交  1-审核中  2-待付款 3-待名称核准 4-待设立登记 5-待用户签名 6-待经办人签名 7-待领证 8-已完成  开票： 0-待创建 1-待付款 2-待审核 3-出票中 4-待发货 5-出库中 6-待收货 7-已签收")
    private Integer orderStatus;

    /**
     * 一级推广人账号
     */
    @ApiModelProperty(value = "一级推广人账号")
    private String accountFirst;

    /**
     * 一级推广人等级
     */
    @ApiModelProperty(value = "一级推广人等级")
    private Integer levelFirst;

    /**
     * 二级推广人账号
     */
    @ApiModelProperty(value = "二级推广人账号")
    private String accountTwo;

    /**
     * 二级推广人等级
     */
    @ApiModelProperty(value = "二级推广人等级")
    private Integer levelTwo;

    /**
     * 城市合伙人
     */
    @ApiModelProperty(value = "城市合伙人")
    private String cityProviders;

    /**
     * 高级城市合伙人
     */
    @ApiModelProperty(value = "高级城市合伙人")
    private String cityPartner;

    /**
     * 机构名称
     */
    @ApiModelProperty(value = "机构名称")
    private String oemName;

    /**
     * 发票类型 1-普通发票 2-增值税发票
     */
    @ApiModelProperty(value = "发票类型 1-普通发票 2-增值税发票")
    private Integer invoiceType;

    /**
     * 发票方式 1-纸质发票 2-电子发票
     */
    @ApiModelProperty(value = "发票方式 1-纸质发票 2-电子发票")
    private Integer invoiceWay;

    /**
     * 开票金额
     */
    @ApiModelProperty(value = "开票金额")
    private Long invoiceAmount;

    /**
     * 增值税费
     */
    @ApiModelProperty(value = "增值税费")
    private Long vatFee;

    /**
     * 个人所得税
     */
    @ApiModelProperty(value = "个人所得税")
    private Long personalIncomeTax;

    /**
     * 邮寄费金额
     */
    @ApiModelProperty(value = "邮寄费金额")
    private Long postageFees;

    /**
     * 服务费
     */
    @ApiModelProperty(value = "服务费")
    private Long serviceFee;

    /**
     * 服务费优惠
     */
    @ApiModelProperty(value = "服务费优惠")
    private Long serviceFeeDiscount;

    /**
     * 产品名称
     */
    @ApiModelProperty(value = "产品名称")
    private String prodName;

    /**
     * 产品类型 1-个体开户 2-个独开户 3-有限合伙 4-有限责任 5-开票 6-税务顾问 7-城市服务商
     */
    @ApiModelProperty(value = "产品类型 1-个体开户 2-个独开户 3-有限合伙 4-有限责任 5-开票 6-税务顾问 7-城市服务商")
    private Integer prodType;

    /**
     * 通知次数
     */
    @ApiModelProperty(value = "通知次数")
    private Integer alertNumber;

    /**
     * 订单类型
     */
    @ApiModelProperty(value = "订单类型")
    private Integer orderType;

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
    private Long parentMemberId;
    /**
     * 产品邀请人
     */
    private String parentMemberAccount;

    /**
     * 渠道名称
     */
    private String channelName;

    /**
     * 人群标签名称
     */
    private String crowdLabelName;

    /**
     * H5接入方名称
     */
    private String accessPartyName;

    public OrderVO() {

    }

    public OrderVO(OrderEntity orderEntity, MemberAccountEntity memberAccountEntity, OemEntity oemEntity){
        this.orderNo = orderEntity.getOrderNo();
        this.memberPhone = memberAccountEntity.getMemberPhone();
        this.memberName = memberAccountEntity.getRealName();
        this.orderStatus = orderEntity.getOrderStatus();
        this.oemName = oemEntity.getOemName();
        this.addTime = orderEntity.getAddTime();
    }

    public OrderVO(OrderEntity orderEntity, MemberOrderRelaEntity memberOrderRelaEntity, MemberAccountEntity memberAccountEntity, Integer companyType) {
        this.orderNo = orderEntity.getOrderNo();
        this.memberPhone = memberAccountEntity.getMemberPhone();
        this.memberName = memberAccountEntity.getRealName();
        if (memberOrderRelaEntity != null) {
            this.accountFirst = memberOrderRelaEntity.getAccountFirst();
            this.levelFirst = memberOrderRelaEntity.getLevelFirst();
            this.accountTwo = memberOrderRelaEntity.getAccountTwo();
            this.levelTwo = memberOrderRelaEntity.getLevelTwo();
            this.cityProviders = memberOrderRelaEntity.getCityProvidersName();
            this.cityPartner = memberOrderRelaEntity.getCityPartnerName();
            this.memberLevel = memberOrderRelaEntity.getMemberLevel();

//            this.oemName = memberOrderRelaEntity.getOemName();
        }
        this.addTime = orderEntity.getAddTime();
        this.orderStatus = orderEntity.getOrderStatus();
        this.companyType = companyType;
        this.upDiamondAccount = memberAccountEntity.getUpDiamondAccount();
        this.superDiamondAccount = memberAccountEntity.getSuperDiamondAccount();
        this.attributionEmployeesAccount = memberAccountEntity.getAttributionEmployeesAccount();
    }

}