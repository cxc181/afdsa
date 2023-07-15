package com.yuqian.itax.pay.entity.vo;

import lombok.Getter;
import lombok.Setter;
import org.jeecgframework.poi.excel.annotation.Excel;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Setter
@Getter
public class RechargeVO implements Serializable {

    private static final long serialVersionUID = -1L;


    /**
     * 订单号
     */
    @Excel(name = "订单编号")
    private String orderNo;

    /**
     * 订单类型 1-充值 2-代理充值 3-提现 4-代理提现 5 - 工商开户 6-开票
     */
    @Excel(name = "订单类型" , replace = { "充值_1","代理充值_2","提现_3","代理提现_4","工商开户_5","开票_6" })
    private Integer orderType;

    /**
     * 账户角色
     */
    @Excel(name = "账户角色")
    private String roleName;

    /**
     * 钱包类型 1-消费钱包 2-佣金钱包
     */
    @Excel(name = "钱包类型", replace = { "消费钱包_1","佣金钱包_2" })
    private Integer walletType;

    /**
     * 充值账号
     */
    @Excel(name = "充值账号")
    private String account;

    /**
     * 绑定手机号
     */
    @Excel(name = "绑定手机号")
    private String phone;

    /**
     * 用户名称
     */
    @Excel(name = "用户名称")
    private String userName;

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
     * 充值金额（元）
     */
    @Excel(name = "充值金额（元）")
    private BigDecimal payAmount;

    /**
     *     支付方式  1-微信 2-余额 3-支付宝 4-快捷支付
     */
    @Excel(name = "支付方式" , replace = { "微信_1","余额_2","支付宝_3","快捷支付_4" } )
    private Integer payWay;

    /**
     * 支付账号
     */
    @Excel(name = "支付账号")
    private String payAccount;

    /**
     * 银行名称
     */
    @Excel(name = "银行名称")
    private String payBank;

    /**
     * 订单状态 0-待支付 1-支付中 2 -支付成功 3-支付失败
     */
    @Excel(name = "订单状态" , replace = { "待支付_0","支付中_1","支付成功_2","支付失败_3","待财务审核_4","财务审核失败_5" })
    private Integer payStatus;
    /**
     * OEM机构
     */
    @Excel(name = "所属OEM")
    private String oemName;
}
