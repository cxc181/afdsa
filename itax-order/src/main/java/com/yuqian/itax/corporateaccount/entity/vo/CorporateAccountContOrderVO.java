package com.yuqian.itax.corporateaccount.entity.vo;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.jeecgframework.poi.excel.annotation.Excel;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 对公户续费订单列表
 */
@Getter
@Setter
public class CorporateAccountContOrderVO implements Serializable {

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 订单编号
     */
    @Excel(name = "订单编号")
    private String orderNo;

    /**
     * 注册账号
     */
    @Excel(name = "注册账号")
    private String memberAccount;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 企业名称
     */
    @Excel(name = "企业名称")
    private String companyName;

    /**
     * 企业id
     */
    private Long companyId;

    /**
     * 经营者姓名
     */
    @Excel(name = "经营者姓名")
    private String operatorName;

    /**
     * 对公户开户行
     */
    @Excel(name = "开户行")
    private String corporateAccountBankName;

    /**
     * 银行账号
     */
    @Excel(name = "银行账号")
    private String corporateAccount;

    /**
     * 续费金额
     */
    @Excel(name = "续费金额")
    private BigDecimal orderAmount;

    /**
     * 支付金额
     */
    @Excel(name = "支付金额")
    private BigDecimal payAmount;

    /**
     * 订单状态
     */
    @Excel(name = "订单状态",replace = { "-_null", "待支付_0", "等待预约_1","已完成_2","已取消_3"})
    private Integer orderStatus;

    /**
     * 园区名称
     */
    @Excel(name = "园区")
    private String parkName;

    /**
     * oem机构名称
     */
    @Excel(name = "OEM机构")
    private String oemName;

    /**
     * 创建时间
     */
    @Excel(name = "创建时间", replace = {
            "-_null" }, databaseFormat = "yyyyMMddHHmmss", format = "yyyy-MM-dd HH:mm:ss", width = 20)
    private Date addTime;

    /**
     * 完成时间
     */
    @Excel(name = "完成时间", replace = {
            "-_null" }, databaseFormat = "yyyyMMddHHmmss", format = "yyyy-MM-dd HH:mm:ss", width = 20)
    private Date updateTime;


    public void setCorporateAccount(String corporateAccount){
        if (StringUtils.isNotBlank(corporateAccount)) {
            this.corporateAccount = StringUtils.overlay(corporateAccount, "****", 4, corporateAccount.length() - 4);
        }
    }

}
