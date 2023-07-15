package com.yuqian.itax.capital.entity.query;

import com.yuqian.itax.common.base.entity.query.BaseQuery;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

/**
 * 用户资金账号表
 * 
 * @Date: 2019年12月07日 20:54:06 
 * @author 蒋匿
 */
@Getter
@Setter
public class UserCapitalAccountQuery extends BaseQuery {
	
	private static final long serialVersionUID = -1L;

	/**
	 * 机构编码
	 */
	private String oemCode;

	/**
	 * 机构名称
	 */
	private String oemName;

	/**
	 * 用户id
	 */
	private Long userId;

	/**
	 * 用户类型 1-会员 2-城市合伙人 3-合伙人 4-平台 5-管理员 6-其他
	 */
	private Integer userType;

	/**
	 * 用户不包含类型 1-会员 2-城市合伙人 3-合伙人 4-平台 5-管理员 6-其他
	 */
	private String userTypeNotIn;

	//=====================资金账号分页查询====================
	/**
	 * 账号
	 */
	private String username;
	/**
	 * 绑定手机
	 */
	private String phone;
	/**
	 * 用户昵称
	 */
	private String nickname;
	/**
	 * 用户角色
	 */
	private String roleName;
	/**
	 * 钱包类型 1-消费钱包 2-佣金钱包
	 */
	private Integer walletType;

	private String tree;

	/**
	 * 平台类型  1-平台 2-机构 3-园区 4-城市合伙人 5-城市合伙人
	 */
	private Integer platformType;
	/**
	 * 账号类型  1-管理员  2-坐席客服 3-普通用户
	 */
	private Integer accountType;
	/**
	 *账号编号
	 */
	private String capitalAccount;
}
