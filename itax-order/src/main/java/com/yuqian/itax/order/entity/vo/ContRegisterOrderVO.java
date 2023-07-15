package com.yuqian.itax.order.entity.vo;

import lombok.Getter;
import lombok.Setter;
import org.jeecgframework.poi.excel.annotation.Excel;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 企业续费订单实体类
 * @author：pengwei
 * @Date：2021/2/4 9:53
 * @version：1.0
 */
@Getter
@Setter
public class ContRegisterOrderVO implements Serializable {

    /**
     * 订单号
     */
    @Excel(name = "订单编号", width = 24)
    private String orderNo;

    /**
     * 会员手机号
     */
    @Excel(name = "注册账号", width = 16)
    private String memberPhone;

    /**
     * 用户身份
     */
    @Excel(name = "用户身份", replace = { "-_null","未实名_0","个人_1","企业_2" }, height = 10, width = 22)
    private Integer memberAuthType;

    /**
     * 企业名称
     */
    @Excel(name = "企业名称", width = 20)
    private String companyName;

    /**
     * 会员等级 -1-员工 0-普通会员 1-VIP 2-白银会员 3-税务顾问 4-铂金会员 5-城市服务商
     */
    @Excel(name = "会员等级", replace = { "-_null","员工_-1","普通会员_0","VIP_1","白银会员_2","税务顾问_3","铂金会员_4","城市服务商_5" }, width = 20)
    private Integer memberLevel;

    /**
     * 企业类型 1-个体工户 2-个人独资企业 3-有限合伙公司 4-有限责任公司
     */
    @Excel(name = "企业类型", replace = { "-_null","个体户_1","个人独资企业_2","有限合伙公司_3","有限责任公司_4" }, width = 22)
    private Integer companyType;

    /**
     * 续费类型 1-托管费 2-对公户
     */
    @Excel(name = "续费类型", replace = { "-_null","托管费_1","对公户_2" })
    private Integer contType;

    /**
     * 订单金额（续费金额）
     */
    @Excel(name = "续费金额")
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
     * 订单状态 0-待支付 1-支付中 2-已完成 3-已取消
     */
    @Excel(name = "订单状态", replace = { "-_null","待支付_0","支付中_1","已完成_2","已取消_3" })
    private Integer orderStatus;

    /**
     * 推广人账号
     */
   // @Excel(name = "推广人手机号")
    private String accountFirst;

    /**
     * 推广人会员等级 -1-员工 0-普通会员 1-VIP 2-白银会员 3-税务顾问 4-铂金会员 5-城市服务商
     */
   // @Excel(name = "推广人会员等级", replace = { "-_null","员工_-1","普通会员_0","VIP_1","白银会员_2","税务顾问_3","铂金会员_4","城市服务商_5" }, width = 20)
    private Integer levelFirst;

    /**
     * 园区名称
     */
    @Excel(name = "园区")
    private String parkName;

    /**
     * 上级城市服务商手机号(所属城市服务商手机号)
     */
   // @Excel(name = "所属服务商手机号", width = 16)
    private String upDiamondAccount;

    /**
     * 城市合伙人
     */
   // @Excel(name = "城市合伙人")
    private String cityProviders;

    /**
     * 高级城市合伙人
     */
   // @Excel(name = "高级城市合伙人", width = 16)
    private String cityPartner;

    /**
     * 所属员工手机号
     */
   // @Excel(name = "所属员工手机号")
    private String attributionEmployeesAccount;

    /**
     * 上上级城市服务商账号(上上级城市服务商手机号)
     */
   // @Excel(name = "上上级服务商手机号")
    private String superDiamondAccount;
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
     * 渠道code
     */
    private String channelCode;
    /**
     * 推送状态：0-待推送 1-推送中 2-已推送 3-推送失败 4-无需推送
     */
    @Excel(name = "推送状态", replace = { "-_null","待推送_0","推送中_1","已推送_2","推送失败_3","无需推送_4","_null" }, width = 20)
    private Integer channelPushState;

    /**
     * 邀请人账号
     */
    @Excel(name = "产品邀请人", width = 16)
    private String parentMemberAccount;

    /**
     * 机构名称
     */
    @Excel(name = "OEM机构")
    private String oemName;

}
