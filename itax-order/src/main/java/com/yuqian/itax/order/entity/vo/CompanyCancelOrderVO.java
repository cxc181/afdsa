package com.yuqian.itax.order.entity.vo;

import lombok.Getter;
import lombok.Setter;
import org.jeecgframework.poi.excel.annotation.Excel;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 企业注销订单返回实体
 * @author：pengwei
 * @Date：2020/2/15 16:32
 * @version：1.0
 */
@Getter
@Setter
public class CompanyCancelOrderVO implements Serializable {

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
     * 用户姓名
     */
    @Excel(name = "用户姓名")
    private String realName;

    /**
     * 用户昵称
     */
    private String memberName;

    /**
     * 会员等级 -1-员工 0-普通会员 1-VIP 2-白银会员 3-税务顾问 4-铂金会员 5-城市服务商
     */
    @Excel(name = "会员等级", replace = { "员工_-1","普通会员_0","VIP_1","白银会员_2","税务顾问_3","铂金会员_4","城市服务商_5" }, height = 10, width = 22)
    private Integer memberLevel;

    /**
     * 用户身份
     */
    @Excel(name = "用户身份", replace = { "-_null","未实名_0","个人_1","企业_2" }, height = 10, width = 22)
    private Integer memberAuthType;

    /**
     * 企业名称
     */
    @Excel(name = "企业名称")
    private String companyName;

    /**
     * 经营着者姓名
     */
    @Excel(name = "经营者/法人")
    private String operatorName;

    /**
     * 企业类型 1-个体开户 2-个独开户 3-有限合伙 4-有限责任
     */
    @Excel(name = "企业类型", replace = { "个体开户_1","个独开户_2","有限合伙_3","有限责任_4" }, height = 10, width = 22)
    private Integer companyType;

    /**
     * 园区名称
     */
    @Excel(name = "园区")
    private String parkName;

    /**
     * 注销服务费
     */
    @Excel(name = "注销服务费")
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
     * 完成时间
     */
    @Excel(name = "完成时间", replace = {
            "-_null" }, databaseFormat = "yyyyMMddHHmmss", format = "yyyy-MM-dd HH:mm:ss", height = 10, width = 20)
    private Date completeTime;
    /**
     订单状态 工商注销：0-待付款 1-注销处理中 2-注销成功 3-已取消 4-税单待处理
     */
    @Excel(name = "订单状态", replace = { "待付款_0","注销处理中_1","税务注销成功_2","已取消_3","税单待处理_4","工商注销成功_5" }, height = 10, width = 22)
    private Integer orderStatus;

    /**
     * 推广人手机号
     */
    //@Excel(name = "推广人手机号")
    private String accountFirst;
    /**
     * 推广人会员等级 -1-员工 0-普通会员 1-VIP 2-白银会员 3-税务顾问 4-铂金会员 5-城市服务商
     */
    //@Excel(name = "推广人会员等级", replace = { "员工_-1","普通会员_0","VIP_1","白银会员_2","税务顾问_3","铂金会员_4","城市服务商_5" }, height = 10, width = 22)
    private Integer levelFirst;

    /**
     * 所属员工手机号
     */
   // @Excel(name = "所属员工手机号")
    private String attributionEmployeesAccount;

    /**
     * 上级城市服务商手机号(所属城市服务商手机号)
     */
    //@Excel(name = "所属服务商手机号")
    private String upDiamondAccount;

    /**
     * 上上级城市服务商账号(上上级城市服务商手机号)
     */
   // @Excel(name = "上上级服务商手机号")
    private String superDiamondAccount;

    /**
     * 城市合伙人
     */
  //  @Excel(name = "城市合伙人")
    private String cityProviders;

    /**
     * 高级城市合伙人
     */
   // @Excel(name = "高级城市合伙人")
    private String cityPartner;

    /**
     * 经办人账号
     */
    //@Excel(name = "经办人账号")
    private String agentAccount;

    /**
     * 机构名称
     */
    @Excel(name = "OEM机构")
    private String oemName;

    /**
     * 产品邀请人
     */
    private String parentMemberId;
    /**
     * 渠道名称
     */
    @Excel(name = "渠道来源")
    private String channelName;
    /**
     * 推送状态：0-待推送 1-推送中 2-已推送 3-推送失败 4-无需推送
     */
    @Excel(name = "推送状态", replace = { "-_null","待推送_0","推送中_1","已推送_2","推送失败_3","无需推送_4" }, height = 10, width = 22)
    private Integer channelPushState;

    /**
     * 二级推广人账号
     */
    private String accountTwo;

    /**
     * 二级推广人等级
     */
    private Integer levelTwo;

    /**
     * 邀请人账号
     */
    @Excel(name = "产品邀请人")
    private String parentMemberAccount;

    /**
     * 纳税人类型  1-小规模纳税人 2-一般纳税人
     */
    @Excel(name = "纳税性质",replace = { "小规模纳税人_1","一般纳税人_2" })
    private String taxpayerType;

}
