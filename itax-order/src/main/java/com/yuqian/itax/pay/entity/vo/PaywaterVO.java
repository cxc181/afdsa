package com.yuqian.itax.pay.entity.vo;

import lombok.Getter;
import lombok.Setter;
import org.jeecgframework.poi.excel.annotation.Excel;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Setter
@Getter
public class PaywaterVO implements Serializable {

    private static final long serialVersionUID = -1L;
    /**
     * 支付流水id
     */
    private Long id;
    /**
     * 流水号
     */
    @Excel(name = "流水号")
    private String payNo;
    /**
     * 流水类型 1-充值，2-提现 ，3-余额支付，4-第三方支付，5-退款
     */
    @Excel(name = "流水类型" , replace = { "充值_1","提现_2","余额支付_3","第三方支付_4","退款_5", "对公户提现_6","企业退税_7","线下支付_8" })
    private Integer  payWaterType;

    /**
     * 外部流水号
     */
    @Excel(name = "外部流水号")
    private String externalOrderNo;

    /**
     * 用户姓名
     */
    @Excel(name = "用户姓名")
    private String realName;

    /**
     * 用户手机号
     */
    @Excel(name = "用户手机号")
    private String memberPhone;

    /**
     * 订单号
     */
    @Excel(name = "订单号")
    private String orderNo;

    /**
     * 订单类型 1-充值 2-代理充值 3-提现 4-代理提现 5 - 工商注册 6-开票 7-用户升级 8-工商注销 9-证件申请 10-对公户申请 11-对公户提现 12-消费开票 13-补税 14-退税 15-托管费续费
     */
    @Excel(name = "订单类型" , replace = { "充值_1","代理充值_2","提现_3","代理提现_4","工商开户_5","开票_6","用户升级_7","工商注销_8","用章申请_9","对公户申请_10","对公户提现_11","消费开票_12","补税_13","退税_14","托管费续费_15","对公户续费_16" })
    private Integer orderType;


    /**
     * 钱包类型 1-消费钱包 2-佣金钱包
     */
    @Excel(name = "钱包类型" , replace = { "消费钱包_1","佣金钱包_2" })
    private Integer walletType;
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
    private Date updateTime;

    /**
     * 支付时间
     */
    @Excel(name = "支付时间", replace = {
            "-_null" }, databaseFormat = "yyyyMMddHHmmss", format = "yyyy-MM-dd HH:mm:ss", height = 10, width = 20)
    private Date payTime;

    /**
     * 订单金额
     */
    @Excel(name = "订单金额")
    private BigDecimal orderAmount;

    /**
     * 支付金额
     */
    @Excel(name = "支付金额")
    private BigDecimal payAmount;

    /**
     * 手续费
     */
    @Excel(name = "手续费")
    private BigDecimal serviceFee;

    /**
     * 支付方式  1-微信 2-余额 3-支付宝 4-快捷支付
     */
    @Excel(name = "支付方式" , replace = { "微信_1","余额_2","支付宝_3","快捷支付_4","字节跳动_5","线下转账_6" })
    private Integer payWay;

    /**
     * 支付通道 1-微信 2-余额 3-支付宝 4-易宝支付 5-建行 6-北京代付 7-纳呗
     */
    @Excel(name = "支付通道" , replace = { "微信_1","余额_2","支付宝_3","易宝支付_4","建行_5","北京代付_6","纳呗_7","字节跳动支付_8","线下_9","易税_10" })
    private Integer payChannels;
    /**
     * 支付状态 0-待支付 1-支付中 2 -支付成功 3-支付失败
     */
    @Excel(name = "支付状态" , replace = { "待支付_0","支付中_1","支付成功_2","支付失败_3","待财务审核_4", "财务审核失败_5" ,"待财务打款（线下）_6"})
    private Integer payStatus;

    /**
     * 支付账号
     */
    @Excel(name = "支付账号")
    private String payAccount;

    /**
     * 银行名称
     */
    @Excel(name = "支付银行")
    private String payBank;

    /**
     * 所属OEM
     */
    @Excel(name = "所属OEM")
    private String oemName;
    /**
     * 打款凭证
     */
    private String payPic;
    /**
     * 描述
     */
    private String upResultMsg;
    /**
     * 退款状态 1-退款中 2-退款成功 3-退款失败
     */
    private Integer refundStatus;


    /**
     * 收单oem机构名称
     */
    @Excel(name = "收单oem机构")
    private String otherPayOemName;

    /**
     * 收单机构oemcode
     */
    private String otherPayOemcode;

}
