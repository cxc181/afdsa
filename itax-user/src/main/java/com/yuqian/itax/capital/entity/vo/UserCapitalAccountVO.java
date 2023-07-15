package com.yuqian.itax.capital.entity.vo;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.jeecgframework.poi.excel.annotation.Excel;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
public class UserCapitalAccountVO implements Serializable {
    private static final long serialVersionUID = -1L;
    /**
     * 资金账户主键id
     */
    private Long id;

    /**
     * 账户编码
     */
    @Excel(name="账号编码")
    private String capitalAccount;
    /**
     * 账户
     */
    @Excel(name="账号")
    private String username;
    /**
     * 绑定手机
     */
    @Excel(name="绑定手机")
    private String phone;
    /**
     * 名称
     */
    @Excel(name="用户名称")
    private String nickname;
    /**
     * 用户id
     */
    private Long userId;
    /**
     * 用户类型 1-会员 2 -系统用户
     */
    private Integer userType;

    /**
     * 用户角色
     */
    @Excel(name="角色")
    private String roleName;
    /**
     * 可用余额
     */
    @Excel(name="可用余额" )
    private BigDecimal availableAmount;
    /**
     * 总金额
     */
    @Excel(name="总金额" )
    private BigDecimal totalAmount;
    /**
     * 冻结金额
     */
    @Excel(name="冻结金额" )
    private BigDecimal blockAmount;
    /**
     * 待结算金额
     */
    @Excel(name="待结算金额" )
    private BigDecimal outstandingAmount;
    /**
     *钱包类型 1-消费钱包 2-佣金钱包
     */
    @Excel(name="钱包类型" , replace = { "消费钱包_1","佣金钱包_2" })
    private Integer walletType;

    /**
     * 账户状态 0-已冻结 1-正常
     */
    @Excel(name="账户状态" , replace = { "已冻结_0","正常_1" })
    private Integer status;

    /**
     * 机构名称
     */
    @Excel(name="机构名称" )
    private String oemName;
    /**
     * 创建时间
     */
    @Excel(name = "创建时间", replace = { "-_null" }, databaseFormat = "yyyyMMddHHmmss", format = "yyyy-MM-dd HH:mm:ss", width = 20)
    private Date addTime;
}
