package com.yuqian.itax.pay.entity;

import com.yuqian.itax.util.util.SignExclude;
import java.io.Serializable;


public class MerchantBalanceRequestVO implements Serializable{
	private static final long serialVersionUID = 2668557367216019469L;
	
	/**
     * 商户编号
     */
    private String p1_merchantNo;
    
    /**
     * 签名字段
     */
    @SignExclude
    private String p2_hmac;

	public String getP1_merchantNo() {
		return p1_merchantNo;
	}

	public void setP1_merchantNo(String p1_merchantNo) {
		this.p1_merchantNo = p1_merchantNo;
	}

	public String getP2_hmac() {
		return p2_hmac;
	}

	public void setP2_hmac(String p2_hmac) {
		this.p2_hmac = p2_hmac;
	}
	
	
}
