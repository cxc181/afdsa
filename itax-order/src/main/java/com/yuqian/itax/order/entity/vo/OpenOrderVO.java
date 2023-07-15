package com.yuqian.itax.order.entity.vo;

import lombok.Getter;
import lombok.Setter;
import org.jeecgframework.poi.excel.annotation.Excel;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 开户订单返回实体
 * @author：pengwei
 * @Date：2019/12/10 18:52
 * @version：1.0
 */
@Getter
@Setter
public class OpenOrderVO implements Serializable {

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
    @Excel(name = "用户昵称")
    private String memberName;

    /**
     * 用户名
     */
    @Excel(name = "用户名")
    private String realName;

    /**
     * 用户身份
     */
    @Excel(name = "用户身份", replace = { "-_null","未实名_0","个人_1","企业_2" }, height = 10, width = 22)
    private Integer memberAuthType;

    /**
     * 会员等级 -1-员工 0-普通会员 1-VIP 2-白银会员 3-税务顾问 4-铂金会员 5-城市服务商
     */
    @Excel(name = "会员等级", replace = { "-_null","员工_-1","普通会员_0","VIP_1","白银会员_2","税务顾问_3","铂金会员_4","城市服务商_5" }, height = 10, width = 22)
    private Integer memberLevel;

    /**
     * 企业名称
     */
    @Excel(name = "企业名称")
    private String companyName;

    /**
     * 企业类型 1-个体户 2-个人独资企业 3-有限合伙公司 4-有限责任公司
     */
    @Excel(name = "企业类型", replace = { "-_null","个体户_1","个人独资企业_2","有限合伙公司_3","有限责任公司_4" }, height = 10, width = 22)
    private Integer companyType;

    /**
     * 园区id
     */
    private String parkId;

    /**
     * 经营者姓名
     */
    @Excel(name = "经营者/法人")
    private String operatorName;

    /**
     * 园区名称
     */
    @Excel(name = "园区")
    private String parkName;

    /**
     * 订单金额（注册年费）
     */
    @Excel(name = "托管费")
    private BigDecimal orderAmount;

    /**
     * 优惠金额
     */
    @Excel(name = "会员优惠")
    private BigDecimal discountAmount;

    /**
     * 优惠券金额
     */
    @Excel(name = "优惠券抵扣")
    private String faceAmount;

    /**
     * 支付金额
     */
    @Excel(name = "支付金额")
    private BigDecimal payAmount;

    /**
     * 经办人账号
     */
    @Excel(name = "经办人账号")
    private String agentAccount;

    /**
     * 创建时间
     */
    @Excel(name = "创建时间", replace = {
            "-_null" }, databaseFormat = "yyyyMMddHHmmss", format = "yyyy-MM-dd HH:mm:ss", height = 10, width = 20)
    private Date addTime;

    /**
     * 审核通过时间
     */
    @Excel(name = "审核时间" ,replace = {
            "-_null" }, databaseFormat = "yyyyMMddHHmmss", format = "yyyy-MM-dd HH:mm:ss", height = 10, width = 20)
    private Date orderAuditTime;

    /**
     * 完成时间
     */
    @Excel(name = "完成时间", replace = {
            "-_null" }, databaseFormat = "yyyyMMddHHmmss", format = "yyyy-MM-dd HH:mm:ss", height = 10, width = 20)
    private Date completeTime;

    /**
     订单状态 工商注册   0-待电子签字,1-待视 频认证,2-审核中,3-待付款,4-待领证,5-已完成,6-已取消,7-审核失败,8-核名驳回,9-待身份验证，10-待设立登记、11-待提交签名、12-签名待确认 13-待创建
     */
    @Excel(name = "订单状态", replace = { "-_null","待电子签字_0","待视频认证_1","待客服确认_2","待付款_3","待领证_4","已完成_5","已取消_6","审核未通过_7","资料驳回_8","待身份验证_9","待登记_10","待提交签名_11","签名待确认_12","待创建_13" }, height = 10, width = 22)
    private Integer orderStatus;

    /**
     * 纳税性质
     */
    @Excel(name = "纳税性质",replace = { "-_null","小规模纳税人_1","一般纳税人_2" }, height = 10, width = 22)
    private Integer taxpayerType;

    /**
     * 是否已开启身份验证 0-未开启 1-已开启
     */
    @Excel(name = "身份验证状态", replace = { "-_null", "未开启_0", "已开启_1" }, width = 22)
    private Integer isOpenAuthentication;
    /**
     * 经营者/法人手机号
     */
    @Excel(name = "经营者/法人手机号")
    private String operatorTel;
    /**
     * 推广人姓名
     */
    @Excel(name = "推广人姓名")
    private String nameFirst;
    /**
     * 推广人账号
     */
 //  @Excel(name = "推广人手机号")
    private String accountFirst;

    /**
     * 推广人会员等级 -1-员工 0-普通会员 1-VIP 2-白银会员 3-税务顾问 4-铂金会员 5-城市服务商
     */
  //  @Excel(name = "推广人会员等级", replace = { "-_null","员工_-1","普通会员_0","VIP_1","白银会员_2","税务顾问_3","铂金会员_4","城市服务商_5" }, height = 10, width = 22)
    private Integer levelFirst;

    /**
     * 所属员工手机号
     */
   // @Excel(name = "所属员工手机号")
    private String attributionEmployeesAccount;

    /**
     * 上级城市服务商手机号(所属城市服务商手机号)
     */
  //  @Excel(name = "所属服务商手机号")
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
   // @Excel(name = "高级城市合伙人")
    private String cityPartner;

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
     * 接入方名称
     */
    @Excel(name = "接入方名称")
    private String accessPartyName;

    /**
     * 接入方id
     */
    private Long accessPartyId;

    /**
     * 支付方式 1-线上支付 2-线下支付
     */
    @Excel(name = "支付方式", replace = { "-_null","在线支付_1","线下结算_2"}, height = 10, width = 22)
    private String payType;
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
     * 流程标记（1：标准视频认证流程，2：确认身份验证开启流程，3：工商局签名流程）
     */
    private Integer processMark;

    /**
     * t_e_member_account表主键id
     */
    private Long id;
    /**
     * 机构编码
     */
    private String oemCode;

    /**
     * 订单类型
     */
    private Integer orderType;

    /**
     * 邀请人账号
     */
    @Excel(name = "产品邀请人")
    private String parentMemberAccount;

}
