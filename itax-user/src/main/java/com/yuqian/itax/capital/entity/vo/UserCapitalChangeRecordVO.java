package com.yuqian.itax.capital.entity.vo;

import lombok.Getter;
import lombok.Setter;
import org.jeecgframework.poi.excel.annotation.Excel;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Setter
@Getter
public class UserCapitalChangeRecordVO implements Serializable {

    private static final long serialVersionUID = -1L;

    private Long id;

    /**
     * 资金账号编号
     */
    @Excel(name="账号编号")
    private  String capitalAccount;
    /**
     * 用户类型 1-会员 2 -系统用户
     */
    private  Integer userType;
    /**
     * 账户
     */
    @Excel(name="账号")
    private String username;
    /**
     * 机构编码
     */
    private String oemCode;
    /**
     * 机构名称
     */
    @Excel(name="机构名称")
    private String oemName;
    /**
     *钱包类型 1-消费钱包 2-佣金钱包
     */
    @Excel(name="钱包类型" , replace = { "消费钱包_1","佣金钱包_2" })
    private Integer walletType;
    /**
     * 名称
     */
    @Excel(name="用户名称")
    private String nickname;

    /**
     * 发生时间
     */
    @Excel(name = "发生时间", replace = { "-_null" }, databaseFormat = "yyyyMMddHHmmss", format = "yyyy-MM-dd HH:mm:ss", width = 20)
    private Date addTime;

    /**
     * 变动类型  1-收入 2-支出 3-冻结 4-解冻
     */
    @Excel(name="变动类型", replace = { "收入_1","支出_2" ,"冻结_3","解冻_4"})
    private Integer changesType;

    /**
     * 明细描述
     */
    @Excel(name="明细描述")
    private String detailDesc;
    /**
     * 变动前余额
     */
    @Excel(name="变动前余额")
    private BigDecimal changesBeforeAmount;
    /**
     * 变动后余额
     */
    @Excel(name="变动后余额")
    private BigDecimal changesAfterAmount;
    /**
     * 变动金额
     */
    @Excel(name="变动金额")
    private BigDecimal changesAmount;

    /**
     * 订单类型 1-充值 2-代理充值 3-提现 4-代理提现 5 - 工商开户 6-开票 7-会费
     */
    @Excel(name = "订单类型" , replace = { "充值_1","代理充值_2","提现_3","代理提现_4","企业注册_5","开票_6","会费_7" })
    private Integer orderType;

    /**
     * 订单编号
     */
    @Excel(name="订单编号")
    private String orderNo;
}
