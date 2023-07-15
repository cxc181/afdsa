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
public class APIH5SignRespVo extends NabeiAPIBaseResp implements Serializable {

	private static final long serialVersionUID = -4784474197451618211L;

	/**
	 * 签约申请响应状态 1-请求成功，其他失败
	 */
	@SignInclude
	private String p3_status;
	
	/**
	 * 签约申请响应状态描述
	 */
	private String p4_message;
	
	/**
	 * 签约 url
	 */
	private String p5_signUrl;
	
	/**
	 * 签名
	 */
	private String p_hmac;

}
