package com.yuqian.itax.order.entity.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 订单微信授权关系表
 * 
 * @Date: 2021年03月16日 14:52:21 
 * @author 蒋匿
 */
@Getter
@Setter
public class OrderWechatAuthRelaDTO implements Serializable {
	
	private static final long serialVersionUID = -1L;
	
	/**
	 * 订单号
	 */
	private String orderNo;
	
	/**
	 * 微信模板类型 1-工单审核 2-邀请签名 3-签名确认结果
	 */
	private Integer wechatTmplType;
	
	/**
	 * 授权状态 0-未授权 1-已授权
	 */
	private Integer authStatus;

}
