package com.yuqian.itax.nabei.entity;

import com.yuqian.itax.util.util.nabei.SignInclude;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 企业账户查询响应参数实体
 */
@Getter
@Setter
public class MerchantBalanceQueryRespVo extends NabeiAPIBaseResp implements Serializable {

	private static final long serialVersionUID = -4784474197451618211L;

	/**
	 * 账户总余额
	 */
	@SignInclude
	private String p3_totalAmount = "0.00";
	
	/**
	 * 可用总金额
	 */
	@SignInclude
	private String p4_availAmount = "0.00";
	
	/**
	 * 冻结总金额
	 */
	@SignInclude
	private String p5_blockAmount = "0.00";
	
	/**
	 * 账户明细
	 */
	private String p6_detailJson = "[]";
	
	/**
	 * 签名
	 */
	private String p7_hmac;

}
