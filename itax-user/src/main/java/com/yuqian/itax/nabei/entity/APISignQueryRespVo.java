package com.yuqian.itax.nabei.entity;

import com.yuqian.itax.util.util.nabei.SignInclude;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 签约注册查询响应参数实体
 */
@Getter
@Setter
public class APISignQueryRespVo extends NabeiAPIBaseResp implements Serializable {

	private static final long serialVersionUID = -4784474197451618211L;

	/**
	 * 签约请求响应状态 0-未签约，1-已签约，2-已解约
	 */
	@SignInclude
	private String p3_status;
	
	/**
	 * 签约姓名
	 */
	private String p4_name;
	
	/**
	 * 签约身份证号
	 */
	private String p5_idcardNo;
	
	/**
	 * 签约卡号
	 */
	private String p6_accountNo;
	
	/**
	 * 预留手机号
	 */
	private String p7_mobile;
	
	/**
	 * 协议编号
	 */
	private String p8_protocolNo;
	
	/**
	 * 签名
	 */
	private String p9_hmac;

}
