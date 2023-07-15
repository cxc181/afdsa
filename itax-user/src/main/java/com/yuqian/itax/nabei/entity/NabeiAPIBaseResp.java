package com.yuqian.itax.nabei.entity;

import com.yuqian.itax.util.util.nabei.SignInclude;

import java.io.Serializable;

public class NabeiAPIBaseResp implements Serializable{
	private static final long serialVersionUID = 7784009072439777132L;

	@SignInclude
	private String p1_resCode = "0000";
	
	@SignInclude
	private String p2_resMsg = "SUCCESS";

	public String getP1_resCode() {
		return p1_resCode;
	}

	public void setP1_resCode(String p1_resCode) {
		this.p1_resCode = p1_resCode;
	}

	public String getP2_resMsg() {
		return p2_resMsg;
	}

	public void setP2_resMsg(String p2_resMsg) {
		this.p2_resMsg = p2_resMsg;
	}
	
}
