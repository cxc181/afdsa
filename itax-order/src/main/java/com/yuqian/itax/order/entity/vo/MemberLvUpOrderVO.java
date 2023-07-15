package com.yuqian.itax.order.entity.vo;

import lombok.Getter;
import lombok.Setter;
import org.jeecgframework.poi.excel.annotation.Excel;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 会员升级订单返回实体
 * @author：pengwei
 * @Date：2019/12/10 18:52
 * @version：1.0
 */
@Getter
@Setter
public class MemberLvUpOrderVO implements Serializable {
    /**
     * 订单号
     */
    @Excel(name = "订单编号")
    private String orderNo;
    /**
     * 会员手机号
     */
    @Excel(name = "注册账号")
    private String memberPhone;
    /**
     * 用户昵称
     */
    @Excel(name = "用户名")
    private String memberName;

    /**
     * 用户身份
     */
    @Excel(name = "用户身份", replace = { "-_null","未实名_0","个人_1","企业_2" }, height = 10, width = 22)
    private Integer memberAuthType;

    /**
     * 会员等级 -1->员工 0-普通会员  1-税务顾问  2-城市服务商
     */
    private Integer memberLevel;

    /**
     * 购买会员等级 0-普通会员 1-VIP 2-白银会员 3-税务顾问 4-铂金会员 5-城市服务商
     */
    private Integer buyMemberLevel;

    /**
     * 购买会员等级名称
     */
    @Excel(name = "升级会员等级")
    private String buyMemberLevelName;

    /**
     * 订单金额
     */
    @Excel(name = "订单金额")
    private BigDecimal orderAmount;
    /**
     * 优惠金额
     */
    @Excel(name = "优惠金额")
    private BigDecimal discountAmount;
    /**
     * 支付金额
     */
    @Excel(name = "支付金额")
    private BigDecimal payAmount;
    /**
     * 创建时间
     */
    @Excel(name = "创建时间", replace = {
            "-_null" }, databaseFormat = "yyyyMMddHHmmss", format = "yyyy-MM-dd HH:mm:ss", height = 10, width = 20)
    private Date addTime;
    /**
     订单状态
     会员升级： 0-待支付 1-支付中 2-财务审核 3-已完成 4-已取消\r\n
     */
    @Excel(name = "订单状态", replace = { "待支付_0","支付中_1","财务审核_2","已完成_3","已取消_4" }, height = 10, width = 22)
    private Integer orderStatus;
    /**
     * 一级推广人账号
     */
   // @Excel(name = "推广人手机号")
    private String accountFirst;
    /**
     * 一级推广人等级 -1-员工 0-普通会员 1-VIP 2-白银会员 3-税务顾问 4-铂金会员 5-城市服务商
     */
   // @Excel(name = "推广人会员等级", replace = { "员工_-1","普通会员_0","VIP_1","白银会员_2","税务顾问_3","铂金会员_4","城市服务商_5" }, height = 10, width = 22)
    private Integer levelFirst;
    /**
     * 二级推广人账号
     */
    private String accountTwo;
    /**
     * 二级推广人等级
     */
    private Integer levelTwo;
    /**
     * 产品名称
     */
    private String prodName;
    /**
     * 推广类型 1-散客 2-直客 3-顶级直客
     */
    private Integer extendType;

    /**
     * 产品邀请人
     */
    private String parentMemberId;
    /**
     * 邀请人账号
     */
    @Excel(name = "产品邀请人", width = 16)
    private String parentMemberAccount;
    /**
     * 渠道名称
     */
    @Excel(name = "渠道来源")
    private String channelName;
    /**
     * 推送状态：0-待推送 1-推送中 2-已推送 3-推送失败 4-无需推送
     */
    @Excel(name = "推送状态", replace = { "-_null","待推送_0","推送中_1","已推送_2","推送失败_3","无需推送_4","_null" }, width = 20)
    private Integer channelPushState;

    /**
     * 所属员工手机号
     */
   // @Excel(name = "所属员工手机号")
    private String attributionEmployeesAccount;

    /**
     * 上级城市服务商手机号(所属城市服务商手机号)
     */
   // @Excel(name = "所属服务商手机号")
    private String upDiamondAccount;

    /**
     * 上上级城市服务商账号(上上级城市服务商手机号)
     */
   // @Excel(name = "上上级服务商手机号")
    private String superDiamondAccount;

    /**
     * 城市合伙人
     */
   // @Excel(name = "城市合伙人")
    private String cityProviders;

    /**
     * 高级城市合伙人
     */
  //  @Excel(name = "高级城市合伙人")
    private String cityPartner;

    /**
     * 机构名称
     */
    @Excel(name = "OEM机构")
    private String oemName;
}
