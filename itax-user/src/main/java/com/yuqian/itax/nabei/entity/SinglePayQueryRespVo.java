package com.yuqian.itax.nabei.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.yuqian.itax.util.util.nabei.SignInclude;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 单笔出款查询响应参数实体
 */
@Getter
@Setter
public class SinglePayQueryRespVo extends NabeiAPIBaseResp implements Serializable {

	private static final long serialVersionUID = -4784474197451618211L;

	/**
	 * 订单号
	 */
	@SignInclude
	private String p3_orderNo;
	
	/**
	 * 订单时间
	 */
	@SignInclude
	@JSONField(format = "yyyy-MM-dd HH:mm:ss")
	private String p4_orderTime;
	
	/**
	 * 订单金额
	 */
	@SignInclude
	private String p5_amount;
	
	/**
	 * 收款户名
	 */
	@SignInclude
	private String p6_accountName;
	
	/**
	 * 收款账号
	 */
	@SignInclude
	private String p7_accountNo;
	
	/**
	 * 代发处理状态 0-等待业务处理，1-业务系统出款中，2-出款成功，3-出款失败,9-待签约
	 */
	@SignInclude
	private String p8_status;
	
	/**
	 * 处理状态描述
	 */
	@SignInclude
	private String p9_message;
	
	/**
	 * 签名
	 */
	private String p10_hmac;

}
