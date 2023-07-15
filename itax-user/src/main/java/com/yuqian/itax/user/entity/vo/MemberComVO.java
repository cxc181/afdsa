package com.yuqian.itax.user.entity.vo;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 *  @Author: Kaven
 *  @Date: 2020/6/5 9:42
 *  @Description: 推广中心-会员企业开票统计信息VO
 */
@Getter
@Setter
public class MemberComVO implements Serializable {
	
	private static final long serialVersionUID = -1L;
	/**
	 * 公司ID
	 */
	private Long companyId;

	/**
	 * 公司名称
	 */
	private String companyName;

	/**
	 * 企业状态 1-正常 2-禁用 4-已注销 5-注销中
	 */
	private Integer status;

	/**
	 * 累计开票
	 */
	private Long totalInvoiceAmount;

	/**
	 * 本年开票
	 */
	private Long yearInvoiceAmount;

	/**
	 * 本月开票
	 */
	private Long monthInvoiceAmount;
}
