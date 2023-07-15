package com.yuqian.itax.pay.entity;


import com.yuqian.itax.util.util.SignExclude;
import java.io.Serializable;

public class RepayQueryRequestVO implements Serializable{

	private static final long serialVersionUID = -5266034031519471159L;
	
	/**
     * 商户编号
     */
    private String p1_merchantNo;

    /**
     * 代付批次号
     */
    private String p2_batchNo;

    /**
     * 订单号可空，多订单号以逗号分隔
     */
    private String p3_orderNo;

    /**
     * 打款时间
     */
    private String p4_orderTime;

    /**
     * 签名字段
     */
    @SignExclude
    private String p5_hmac;

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

	public String getP3_orderNo() {
		return p3_orderNo;
	}

	public void setP3_orderNo(String p3_orderNo) {
		this.p3_orderNo = p3_orderNo;
	}

	public String getP4_orderTime() {
		return p4_orderTime;
	}

	public void setP4_orderTime(String p4_orderTime) {
		this.p4_orderTime = p4_orderTime;
	}

	public String getP5_hmac() {
		return p5_hmac;
	}

	public void setP5_hmac(String p5_hmac) {
		this.p5_hmac = p5_hmac;
	}

}
