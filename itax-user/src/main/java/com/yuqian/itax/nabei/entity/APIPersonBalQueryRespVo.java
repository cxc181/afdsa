package com.yuqian.itax.nabei.entity;

import com.yuqian.itax.util.util.nabei.SignInclude;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 用户额度查询响应参数实体
 */
@Getter
@Setter
public class APIPersonBalQueryRespVo extends NabeiAPIBaseResp implements Serializable {

	private static final long serialVersionUID = -4784474197451618211L;

	/**
	 * 服务商编号
	 */
	@SignInclude
	private String p3_taxNo;
	
	/**
	 * 用户身份证号
	 */
	@SignInclude
	private String p4_idcardNo;
	
	/**
	 * 限额
	 */
	@SignInclude
	private String p5_quotaAmount;
	
	/**
	 * 限额周期起始时间
	 */
	@SignInclude
	private String p6_quotaStart;
	
	/**
	 * 限额周期结束时间
	 */
	@SignInclude
	private String p7_quotaEnd;
	
	/**
	 * 已使用/出款金额
	 */
	@SignInclude
	private String p8_paidAmount;
	
	/**
	 * 剩余可出款金额
	 */
	@SignInclude
	private String p9_remainAmount;
	
	/**
	 * 签名
	 */
	private String p10_hmac;

}
