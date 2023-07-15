package com.yuqian.itax.corporateaccount.query;

import com.yuqian.itax.common.base.entity.query.BaseQuery;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

/**
 *  对公户提现订单订单查询条件实体
 * 
 * @Date: 2020年09月07日 09:12:30 
 * @author 蒋匿
 */
@Getter
@Setter
public class CorporateAccountWithdrawOrderQuery extends BaseQuery implements Serializable {
	
	private static final long serialVersionUID = -1L;
	
	/**
	 * 订单编号
	 */
	private String orderNo;

	/**
	 * 会员账号
	 */
	private String memberPhone;

	/**
	 * 企业名称(账户名)
	 */
	private String companyName;

	/**
	 * 会员等级 -1-员工 0-普通会员 1-VIP 2-白银会员 3-税务顾问 4-铂金会员 5-城市服务商
	 */
	private Integer memberLevel;

	/**
	 * 添加时间（开始）
	 */
	private Date addTimeBeg;

	/**
	 * 添加时间（结束）
	 */
	private Date addTimeEnd;

	/**
	 * 到账人姓名
	 */
	private String arriveUserName;

	/**
	 * 订单状态 (0-待提交,1-支付、提现中,2-支付、提现完成,3-支付、提现失败,4-订单过期,5-已取消,6-待财务审核,7-财务审核失败)
	 */
	private Integer orderStatus;

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
