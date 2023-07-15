package com.yuqian.itax.user.entity.vo;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

/**
 *  @Author: Kaven
 *  @Date: 2020/6/5 9:42
 *  @Description: 推广中心-直推用户信息展示VO
 */
@Getter
@Setter
public class ExtendUserVO implements Serializable {
	
	private static final long serialVersionUID = -1L;
	/**
	 * 用户ID
	 */
	private Long userId;

	/**
	 * 用户姓名
	 */
	private String userName;

	/**
	 * 用户手机号
	 */
	private String memberAccount;

	/**
	 * 推广个体数
	 */
	private Long extendCompanyCount;

	/**
	 * 直推用户数
	 */
	private Long extendUserCount;


	/**
	 * 本年开票
	 */
	private Long yearInvoiceAmount;

	/**
	 * 本月开票
	 */
	private Long monthInvoiceAmount;

	/**
	 * 累计开票
	 */
	private Long totalInvoiceAmount;

	/**
	 * 推广角色类型 1-散客 2-直客 3-顶级直客
	 */
	private Integer extendType;

	/**
	 * 最近一次开票时间
	 */
	private Date lastInvoiceTime;

	/**
	 * 最近一次开票金额
	 */
	private Long lastInvoiceAmount;

	/**
	 * 备注
	 */
	private String remark;
}
