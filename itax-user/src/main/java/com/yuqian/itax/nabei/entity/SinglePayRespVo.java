package com.yuqian.itax.nabei.entity;

import com.yuqian.itax.util.util.nabei.SignInclude;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 单笔出款申请响应参数实体
 */
@Getter
@Setter
public class SinglePayRespVo extends NabeiAPIBaseResp implements Serializable {

	private static final long serialVersionUID = -4784474197451618211L;

	@SignInclude
	private String p3_orderNo;
	
	/**
	 * 代发处理状态 0-等待业务系统处理，1-业务系统出款中，2-出款成功，3-出款失败，9-待签约
	 */
	@SignInclude
	private String p4_status;
	
	/**
	 * 处理状态描述
	 */
	@SignInclude
	private String p5_message;
	
	/**
	 * 签名
	 */
	private String p6_hmac;

}
