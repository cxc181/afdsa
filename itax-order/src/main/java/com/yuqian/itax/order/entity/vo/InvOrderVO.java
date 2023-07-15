package com.yuqian.itax.order.entity.vo;

import lombok.Getter;
import lombok.Setter;
import org.jeecgframework.poi.excel.annotation.Excel;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 开票订单返回实体
 * @author：pengwei
 * @Date：2019/12/10 18:52
 * @version：1.0
 */
@Getter
@Setter
public class InvOrderVO implements Serializable {

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
     * 用户昵称
     */
    @Excel(name = "用户昵称", width = 16)
    private String memberName;

    /**
     * 用户名
     */
    @Excel(name = "用户姓名", width = 16)
    private String realName;

    /**
     * 企业名称(开票企业)
     */
    @Excel(name = "开票企业", width = 20)
    private String companyName;

    @Excel(name = "企业类型",replace = { "个体工商户_1","个人独资企业_2","有限合伙_3","有限责任_4" })
    private Integer companyType;

    @Excel(name = "纳税人类型",replace = { "小规模纳税人_1","一般纳税人_2" })
    private String taxpayerType;

    /**
     * 经营者姓名
     */
    @Excel(name = "经营者姓名")
    private String operatorName;

    /**
     * 会员等级 -1-员工 0-普通会员 1-VIP 2-白银会员 3-税务顾问 4-铂金会员 5-城市服务商
     */
    @Excel(name = "会员等级", replace = { "-_null","员工_-1","普通会员_0","VIP_1","白银会员_2","税务顾问_3","铂金会员_4","城市服务商_5","_null" })
    private Integer memberLevel;

    /**
     * 发票类型 1-普通发票 2-增值税发票
     */
    @Excel(name = "开票类型", replace = { "-_null","增值税普通发票_1","增值税专用发票_2","_null" }, width = 15)
    private Integer invoiceType;

    /**
     * 开票金额
     */
    @Excel(name = "开票金额")
    private BigDecimal invoiceAmount;

    /**
     * 增值税率
     */
    @Excel(name = "增值税率(%)")
    private BigDecimal vatFeeRate;

    /**
     * 增值税费
     */
    @Excel(name = "增值税",replace = { "--_null" })
    private BigDecimal vatFee;

    /**
     * 增值税补缴
     */
//    @Excel(name = "增值税补缴", width = 12)
    private BigDecimal vatPayment;

    /**
     * 附加税
     */
    @Excel(name = "附加税",replace = { "--_null" })
    private BigDecimal surcharge;
    /**
     * 附加税补缴
     */
//    @Excel(name = "附加税补缴", width = 12)
    private BigDecimal surchargePayment;

    /**
     * 个人所得税
     */
    @Excel(name = "所得税",replace = { "--_null" })
    private BigDecimal personalIncomeTax;

    /**
     * 所得税补缴
     */
//    @Excel(name = "所得税补缴", width = 12)
    private BigDecimal incomeTaxPayment;

    /**
     * 应退所得税
     */
//    @Excel(name = "应退所得税", width = 12)
    private BigDecimal refundTaxFee;

    /**
     * 邮寄费金额
     */
    @Excel(name = "邮费")
    private BigDecimal postageFees;

    /**
     * 服务费
     */
    @Excel(name = "服务费")
    private BigDecimal serviceFee;

    /**
     * 服务费优惠
     */
    @Excel(name = "服务费优惠", width = 12)
    private BigDecimal serviceFeeDiscount;

    /**
     * 支付金额
     */
    @Excel(name = "支付金额")
    private BigDecimal payAmount;

    /**
     * 1-线上支付 2-线下支付
     */
    @Excel(name = "支付方式", replace = { "-_null","线上支付_1","线下支付_2"}, width = 15)
    private String payType;

    /**
     * 创建时间
     */
    @Excel(name = "创建时间", replace = {
            "-_null" }, databaseFormat = "yyyyMMddHHmmss", format = "yyyy-MM-dd HH:mm:ss", width = 20)
    private Date addTime;

    /**
     * 开票审核通过时间（开票审核时间）
     */
    @Excel(name = "开票审核时间", replace = {
            "-_null" }, databaseFormat = "yyyyMMddHHmmss", format = "yyyy-MM-dd HH:mm:ss", width = 20)
    private Date orderAuditTime;
    /**
     * 流水通过时间
     */
    @Excel(name = "流水通过时间", replace = {
            "-_null" }, databaseFormat = "yyyyMMddHHmmss", format = "yyyy-MM-dd HH:mm:ss", height = 10, width = 20)
    private Date orderFlowTime;
    /**
     * 开票完成时间（已完成时间）
     */
    @Excel(name = "已完成时间", replace = {
            "-_null" }, databaseFormat = "yyyyMMddHHmmss", format = "yyyy-MM-dd HH:mm:ss", width = 20)
    private Date completeTime;

    /**
     订单状态 开票：  0-待创建 1-待付款 2-待审核 3-出票中 4-待发货 5-出库中 6-待收货 7-已签收  8-已取消 9-待出款 10-审核未通过 11 -待财务审核
     */
    @Excel(name = "订单状态", replace = { "-_null","待创建_0","待付款_1","待客服审核_2","出票中_3","待发货_4","出库中_5","待收货_6","已签收_7","已取消_8","待出款_9","审核未通过_10","待财务审核_11","_null" }, width = 15)
    private Integer orderStatus;

    /**
     * 流水状态 0-待上传 1-审核中 2-审核通过 3-审核不通过 4-流水前置
     */
    @Excel(name = "流水状态", replace = { "-_null","待上传_0","审核中_1","审核通过_2","审核不通过_3","流水前置_4","_null" }, width = 15)
    private Integer bankWaterStatus;

    /**
     * 成果状态 0-无需上传 1-成果前置 2-待上传 3-审核中 4-审核不通过 5-审核通过
     */
    @Excel(name = "成果状态", replace = { "-_null","无需上传_0","成果前置_1","待上传_2","审核中_3","审核不通过_4","审核通过_5" }, width = 15)
    private String achievementStatus;

    /**
     * 开票方式 1-自助开票 2-集团代开 3-佣金开票(数据库)
     * 开票方式 1-自助开票 2-集团批量开票 3-佣金开票（返回页面）
     */
    @Excel(name = "开票方式", replace = { "-_null","自助开票_1","集团批量开票_2","佣金开票_3","_null" })
    private Integer createWay;
    /**
     * 推广人姓名
     */
    @Excel(name = "推广人姓名", width = 16)
    private String nameFirst;
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
    @Excel(name = "推送状态", replace = { "-_null","待推送_0","推送中_1","已推送_2","推送失败_3","无需推送_4","_null" }, width = 20)
    private Integer channelPushState;
    /**
     * 一级推广人账号（推广人账号）
     */
    //@Excel(name = "推广人手机号", width = 16)
    private String accountFirst;
    /**
     * 一级推广人等级（推广人会员等级）-1-员工 0-普通会员 1-VIP 2-白银会员 3-税务顾问 4-铂金会员 5-城市服务商
     */
    //@Excel(name = "推广人会员等级", replace = { "-_null","员工_-1","普通会员_0","VIP_1","白银会员_2","税务顾问_3","铂金会员_4","城市服务商_5","_null" }, width = 20)
    private Integer levelFirst;

    /**
     * 所属员工手机号
     */
    //@Excel(name = "所属员工手机号", width = 16)
    private String attributionEmployeesAccount;

    /**
     * 上级城市服务商手机号(所属城市服务商手机号)
     */
   // @Excel(name = "所属服务商手机号", width = 16)
    private String upDiamondAccount;

    /**
     * 上上级城市服务商账号(上上级城市服务商手机号)
     */
    //@Excel(name = "上上级服务商手机号", width = 16)
    private String superDiamondAccount;

    /**
     * 城市合伙人
     */
    //@Excel(name = "城市合伙人")
    private String cityProviders;

    /**
     * 高级城市合伙人
     */
   // @Excel(name = "高级城市合伙人", width = 20)
    private String cityPartner;

    /**
     * 发票方式 1-纸质发票 2-电子发票
     */
    @Excel(name = "发票类型", replace = { "-_null","纸质发票_1","电子发票_2" })
    private Integer invoiceWay;

    /**
     * 园区名称
     */
    @Excel(name = "园区", width = 16)
    private String parkName;

    /**
     * 经办人账号
     */
    @Excel(name = "经办人账号", width = 16)
    private String agentAccount;

    /**
     * 机构名称
     */
    @Excel(name = "OEM机构")
    private String oemName;

    /**
     * 经营者姓名
     */
    private String operatorTel;

    /**
     * 集团开票订单号
     */
    private String groupOrderNo;

    /**
     * 二级推广人账号
     */
    private String accountTwo;

    /**
     * 二级推广人等级
     */
    private Integer levelTwo;

    /**
     * 是否已退邮寄费 0-未退 1-已退
     */
    private Integer isRefundPostageFee;

    /**
     * 邀请人账号
     */
    @Excel(name = "产品邀请人", width = 16)
    private String parentMemberAccount;

    /**
     * 发票标识 0-正常 1-已作废/红冲 2-作废重开
     */
    @Excel(name = "发票标识", replace = { "-_null","正常_0","已作废/红冲_1","作废重开_2" })
    private Integer invoiceMark;

    /**
     * 作废时间
     */
    @Excel(name = "作废时间", width = 16)
    private String cancellationTime;

    /**
     * 作废/红冲凭证
     */
    private String cancellationVoucher;

    /**
     * 风险承诺函
     */
    private String riskCommitment;

    public void setCompanyType(Integer companyType){
        if(companyType!=null && companyType!= 1){
            this.vatFee = null;
            this.personalIncomeTax=null;
            this.surcharge = null;
        }
        this.companyType = companyType;
    }

}
