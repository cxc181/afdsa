package com.yuqian.itax.capital.entity.dto;

import com.yuqian.itax.common.base.entity.query.BaseQuery;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * 用户资金账号表
 * 
 * @Date: 2019年12月07日 20:54:06 
 * @author 蒋匿
 */
@Getter
@Setter
public class UserCapitalAccountDTO extends BaseQuery {
	
	private static final long serialVersionUID = -1L;
	/**
	 * 主键id
	 */
	private Long id;

	/**
	 * 用户id
	 */
	private Long userId;

	/**
	 * 用户类型 1-会员 2-城市合伙人 3-合伙人 4-平台 5-管理员 6-其他
	 */
	private Integer userType;

	/**
	 * 增加总金额
	 */
	private Long addTotalAmount;

	/**
	 * 增加可用金额
	 */
	private Long addAvailableAmount;

	/**
	 * 增加冻结金额
	 */
	private Long addBlockAmount;

	/**
	 * 增加待结算金额
	 */
	private Long addOutstandingAmount;

	/**
	 * 减少总金额
	 */
	private Long delTotalAmount;

	/**
	 * 减少可用金额
	 */
	private Long delAvailableAmount;

	/**
	 * 减少冻结金额
	 */
	private Long delBlockAmount;

	/**
	 * 减少待结算金额
	 */
	private Long delOutstandingAmount;

	/**
	 * 操作人
	 */
	private String updateUser;

	/**
	 * 操作时间
	 */
	private Date updateTime;
}
