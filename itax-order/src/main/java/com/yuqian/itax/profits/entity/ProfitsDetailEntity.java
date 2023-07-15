package com.yuqian.itax.profits.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
/**
 * 分润明细表
 * 
 * @Date: 2019年12月07日 20:16:04 
 * @author 蒋匿
 */
@Getter
@Setter
@Table(name="t_e_profits_detail")
public class ProfitsDetailEntity implements Serializable {
	
	private static final long serialVersionUID = -1L;
	
	/**
	 * 主键id
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	/**
	 * 订单号
	 */
	private String orderNo;
	
	/**
	 * 订单金额
	 */
	private Long orderAmount;
	
	/**
	 * 支付金额
	 */
	private Long payAmount;
	
	/**
	 * 可分润金额
	 */
	private Long availableProfitsAmount;
	
	/**
	 * 用户id
	 */
	private Long userId;
	
	/**
	 * 用户账号
	 */
	private String userAccount;

	/**
	 * 机构编码
	 */
	private String oemCode;

	/**
	 * 机构名称
	 */
	private String oemName;
	
	/**
	 * 用户等级 0-普通会员  1-税务顾问  2-城市服务商
	 */
	private Integer userLevel;
	
	/**
	 * 用户类型  1-会员 2-城市合伙人 3-合伙人 4-平台
	 */
	private Integer userType;
	
	/**
	 * 分润比例
	 */
	private BigDecimal profitsRate;
	
	/**
	 * 分润金额
	 */
	private Long profitsAmount;
	
	/**
	 * 分润状态 0-待分润 1-分润中 2-已分润待结算 3-已分润已结算 4-分润失败
	 */
	private Integer profitsStatus;
	
	/**
	 * 分润时间
	 */
	private Date profitsTime;
	
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
	 * 分润流水号
	 */
	private String profitsNo;

	/**
	 * 订单类型  1-会员升级 2-工商开户 3-开票 4-工商注销 6-托管费续费
	 */
	private Integer orderType;

	/**
	 * 所属员工id
	 */
	private Long attributionEmployeesId;

	/**
	 * 所属员工账号
	 */
	private String attributionEmployeesAccount;

	/**
	 * 钱包类型 1-消费钱包 2-佣金钱包
	 */
	private Integer walletType;

	/**
	 * 分润类型 1-会费 2-托管费 3-开票服务费 4-注销服务费 5-会费返还
	 */
	private Integer profitsType;

	/**
	 * 提现订单号
	 */
	private String withdrawOrderNo;
}
