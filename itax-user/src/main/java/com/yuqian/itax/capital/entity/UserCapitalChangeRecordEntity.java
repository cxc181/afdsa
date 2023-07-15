package com.yuqian.itax.capital.entity;

import java.io.Serializable;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;
/**
 * 用户资金变动记录
 * 
 * @Date: 2019年12月07日 20:54:31 
 * @author 蒋匿
 */
@Getter
@Setter
@Table(name="t_e_user_capital_change_record")
public class UserCapitalChangeRecordEntity implements Serializable {
	
	private static final long serialVersionUID = -1L;
	
	/**
	 * 主键id
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	/**
	 * 资金账户id
	 */
	private Long capitalAccountId;
	
	/**
	 * 用户id
	 */
	private Long userId;

	/**
	 * 用户类型 1-会员 2-系统用户
	 */
	private Integer userType;

	/**
	 * 机构编码
	 */
	private String oemCode;
	
	/**
	 * 变动金额
	 */
	private Long changesAmount;
	
	/**
	 * 变动前余额
	 */
	private Long changesBeforeAmount;
	
	/**
	 * 变动后余额
	 */
	private Long changesAfterAmount;
	
	/**
	 * 变动类型  1-收入 2-支出 3-冻结 4-解冻
	 */
	private Integer changesType;

	/**
	 * 待结算金额
	 */
	private Long outstandingAmount;
	
	/**
	 * 明细描述
	 */
	private String detailDesc;
	
	/**
	 * 订单类型 1-充值 2-代理充值 3-提现 4-代理提现 5 - 工商开户 6-开票 7-会费 8-注销 9-企业资源申请
	 */
	private Integer orderType;
	
	/**
	 * 订单号
	 */
	private String orderNo;
	
	/**
	 * 添加时间
	 */
	private Date addTime;
	
	/**
	 * 添加人
	 */
	private String addUser;
	
	/**
	 * 修改时间
	 */
	private Date updateTime;
	
	/**
	 * 修改人
	 */
	private String updateUser;
	
	/**
	 * 备注
	 */
	private String remark;

	/**
	 * 钱包类型 1-消费钱包 2-佣金钱包
	 */
	private Integer walletType;
	
}
