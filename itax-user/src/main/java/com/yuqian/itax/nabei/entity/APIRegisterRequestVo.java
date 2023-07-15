package com.yuqian.itax.nabei.entity;

import com.yuqian.itax.util.util.nabei.SignInclude;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 签约注册请求参数实体
 */
@Getter
@Setter
public class APIRegisterRequestVo implements Serializable {
	private static final long serialVersionUID = 7476995990063514365L;
	/**
	 * 签约服务商编号
	 */
	@SignInclude
	private String p1_taxNo;
	/**
	 * 签约姓名
	 */
	@SignInclude
	private String p2_name;
	
	/**
	 * 签约身份证号
	 */
	@SignInclude
	private String p3_idcardNo;
	
	/**
	 * 签约卡号
	 */
	@SignInclude
	private String p4_accountNo;
	
	/**
	 * 银行卡预留手机号码
	 */
	private String p5_mobile;
	
	/**
	 * 签名
	 */
	private String p6_hmac;

}
