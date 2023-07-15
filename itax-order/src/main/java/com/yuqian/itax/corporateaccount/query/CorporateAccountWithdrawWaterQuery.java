package com.yuqian.itax.corporateaccount.query;

import com.yuqian.itax.common.base.entity.query.BaseQuery;
import lombok.Getter;
import lombok.Setter;
import org.jeecgframework.poi.excel.annotation.Excel;

import java.io.Serializable;
import java.util.Date;

/**
 *  对公户提现流水查询条件实体
 * 
 * @Date: 2020年09月07日 09:12:30 
 * @author 蒋匿
 */
@Getter
@Setter
public class CorporateAccountWithdrawWaterQuery extends BaseQuery implements Serializable {
	
	private static final long serialVersionUID = -1L;

	/**
	 * 流水号
	 */
	private String payNo;

	/**
	 * 外部流水号
	 */
	private String externalOrderNo;

	/**
	 * 订单编号
	 */
	private String orderNo;

	/**
	 * 企业名称(出款账户名)
	 */
	private String companyName;

	/**
	 * 对公账户(出款账户)
	 */
	private String corporateAccount;

	/**
	 * 添加时间（开始）
	 */
	private Date addTimeBeg;

	/**
	 * 添加时间（结束）
	 */
	private Date addTimeEnd;

	/**
	 * 到账卡号
	 */
	private String arriveBankAccount;

	/**
	 * 所属OEM
	 */
	private String oemName;

	/**
	 * 机构编码
	 */
	private String oemCode;

	/**
	 * 数据权限
	 */
	private String tree;

	/**
	 * 订单类型 1-充值 2-代理充值 3-提现 4-代理提现 5 - 工商注册 6-开票 7-用户升级 8-工商注销 9-证件申请 10-对公户申请 11-对公户提现
	 */
	private Integer orderType;

	/**
	 * 园区id
	 */
	private Long parkId;

}
