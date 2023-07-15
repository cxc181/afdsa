package com.yuqian.itax.pay.entity;

import com.yuqian.itax.util.util.SignExclude;
import java.io.Serializable;


public class ApplyLimitRequestVO implements Serializable{

	private static final long serialVersionUID = -5266034031519471159L;
	
	/**
     * 商户编号
     */
    private String p1_merchantNo;

    /**
     * 申请额度
     */
    private String p2_amount;

    /**
     * 签名字段
     */
    @SignExclude
    private String p3_hmac;

	public String getP1_merchantNo() {
		return p1_merchantNo;
	}

	public void setP1_merchantNo(String p1_merchantNo) {
		this.p1_merchantNo = p1_merchantNo;
	}

	public String getP2_amount() {
		return p2_amount;
	}

	public void setP2_amount(String p2_amount) {
		this.p2_amount = p2_amount;
	}

	public String getP3_hmac() {
		return p3_hmac;
	}

	public void setP3_hmac(String p3_hmac) {
		this.p3_hmac = p3_hmac;
	}
}
