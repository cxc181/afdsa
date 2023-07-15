package com.yuqian.itax.pay.entity;



import com.yuqian.itax.util.util.SignExclude;

import java.io.Serializable;

public class RepayRequestVO implements Serializable{
	
	private static final long serialVersionUID = 3940820075033558852L;

	/**
     * 商户编号
     */
    private String p1_merchantNo;

    /**
     * 代付批次号
     */
    private String p2_batchNo;

    /**
     * 打款总金额
     */
    private String p3_totalAmount;

    /**
     * 打款总笔数
     */
    private String p4_totalCount;

    /**
     * 打款时间
     */
    private String p5_orderTime;

    /**
     * 打款详情-JSON格式
     */
    @SignExclude
    private String p6_detailJson;

    /**
     * 代付结果通知地址
     */
    @SignExclude
    private String p7_notifyUrl;

    /**
     * 签名字段
     */
    @SignExclude
    private String p8_hmac;

    public String getP1_merchantNo() {
        return p1_merchantNo;
    }

    public void setP1_merchantNo(String p1_merchantNo) {
        this.p1_merchantNo = p1_merchantNo;
    }

    public String getP2_batchNo() {
        return p2_batchNo;
    }

    public void setP2_batchNo(String p2_batchNo) {
        this.p2_batchNo = p2_batchNo;
    }

    public String getP3_totalAmount() {
        return p3_totalAmount;
    }

    public void setP3_totalAmount(String p3_totalAmount) {
        this.p3_totalAmount = p3_totalAmount;
    }

    public String getP4_totalCount() {
        return p4_totalCount;
    }

    public void setP4_totalCount(String p4_totalCount) {
        this.p4_totalCount = p4_totalCount;
    }

    public String getP5_orderTime() {
        return p5_orderTime;
    }

    public void setP5_orderTime(String p5_orderTime) {
        this.p5_orderTime = p5_orderTime;
    }

    public String getP6_detailJson() {
        return p6_detailJson;
    }

    public void setP6_detailJson(String p6_detailJson) {
        this.p6_detailJson = p6_detailJson;
    }

    public String getP7_notifyUrl() {
        return p7_notifyUrl;
    }

    public void setP7_notifyUrl(String p7_notifyUrl) {
        this.p7_notifyUrl = p7_notifyUrl;
    }

    public String getP8_hmac() {
        return p8_hmac;
    }

    public void setP8_hmac(String p8_hmac) {
        this.p8_hmac = p8_hmac;
    }

}
