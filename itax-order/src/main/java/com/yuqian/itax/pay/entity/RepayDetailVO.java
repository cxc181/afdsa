package com.yuqian.itax.pay.entity;

import com.yuqian.itax.capital.entity.UserBankCardEntity;
import com.yuqian.itax.util.util.DateUtil;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class RepayDetailVO implements Serializable {
	private static final long serialVersionUID = 3367579827125132591L;
	/**
	 * 出款订单号
	 */
	private String orderNo;
	/**
	 * 出款时间
	 */
    private String orderTime;
    /**
     * 出款金额(元，保留两位小数)
     */
    private String repayAmount;
    /**
     * 账户名
     */
    private String accountName;
    /**
     * 账户号
     */
    private String bankAccount;
    /**
     * 账户类型0-对公，1-对私
     */
    private String bankType;
    /**
     * 银行名称
     */
    private String bankName;
    /**
     * 省份
     */
    private String bankProvince;
    /**
     * 城市
     */
    private String bankCity;

    /**
     * 代付备注
     */
    private String repayRemark = "商户代付";

    public RepayDetailVO() {

    }
    public RepayDetailVO(UserBankCardEntity cardEntity, Long amt, Date date) {
        this.orderNo = System.currentTimeMillis() + "";
        this.orderTime = DateUtil.formatTimesTampDate(date);
        this.repayAmount = new BigDecimal(amt).divide(new BigDecimal("100"), 2, BigDecimal.ROUND_DOWN).toString();
        this.accountName = cardEntity.getUserName();
        this.bankAccount = cardEntity.getBankNumber();
        this.bankType = "1";
        this.bankName = cardEntity.getBankName();
    }

//    public RepayDetailVO(String orderTime, EtcBankAccount account, BigDecimal amount) {
//        this.orderNo = System.currentTimeMillis() + "";
//        this.orderTime = orderTime;
//        this.repayAmount = amount.toString();
//        this.accountName = account.getUserName();
//        this.bankAccount = account.getBankCardNo();
//        this.bankType = "1";
//        this.bankName = account.getBtName();
//    }


    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(String orderTime) {
        this.orderTime = orderTime;
    }

    public String getRepayAmount() {
        return repayAmount;
    }

    public void setRepayAmount(String repayAmount) {
        this.repayAmount = repayAmount;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getBankAccount() {
        return bankAccount;
    }

    public void setBankAccount(String bankAccount) {
        this.bankAccount = bankAccount;
    }

    public String getBankType() {
        return bankType;
    }

    public void setBankType(String bankType) {
        this.bankType = bankType;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getBankProvince() {
        return bankProvince;
    }

    public void setBankProvince(String bankProvince) {
        this.bankProvince = bankProvince;
    }

    public String getBankCity() {
        return bankCity;
    }

    public void setBankCity(String bankCity) {
        this.bankCity = bankCity;
    }

    public String getRepayRemark() {
        return repayRemark;
    }

    public void setRepayRemark(String repayRemark) {
        this.repayRemark = repayRemark;
    }
}

