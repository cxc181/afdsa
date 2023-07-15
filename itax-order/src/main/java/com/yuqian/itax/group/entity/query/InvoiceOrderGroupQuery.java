package com.yuqian.itax.group.entity.query;

import com.yuqian.itax.common.base.entity.query.BaseQuery;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * 集团开票订单
 * 
 * @Date: 2020年03月04日 09:25:55 
 * @author 蒋匿
 */
@Getter
@Setter
public class InvoiceOrderGroupQuery extends BaseQuery {

	/**
	 * 订单号（模糊）
	 */
	private String likeOrderNo;
	
	/**
	 * 抬头公司名称（模糊）
	 */
	private String likeCompanyName;

	/**
	 * 开票类型 1-普通发票 2-增值税发票
	 */
	private Integer invoiceType;

	/**
	 * 开票类目名称（模糊）
	 */
	private String likeCategoryName;

	/**
	 * 开票金额
	 */
	private Long invoiceAmountBeg;

	/**
	 * 开票金额
	 */
	private Long invoiceAmountEnd;

	/**
	 * 创建时间开始
	 */
	private Date addTimeBeg;

	/**
	 * 创建时间结束
	 */
	private Date addTimeEnd;

	/**
	 * 订单状态 0-流水解析中 1-出票中 2-已签收 3-已取消
	 */
	private Integer orderStatus;

	/**
	 * 添加人（模糊）
	 */
	private String likeAddUser;

	/**
	 * OEM机构（模糊）
	 */
	private String likeOemName;

	/**
	 * 付款截图
	 */
	private String payImgUrl;

	/**
	 * 审核备注
	 */
	private String auditDesc;
}
