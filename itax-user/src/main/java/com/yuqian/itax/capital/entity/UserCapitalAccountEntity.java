package com.yuqian.itax.capital.entity;

import java.io.Serializable;
import java.util.Date;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;
/**
 * 用户资金账号表
 * 
 * @Date: 2019年12月07日 20:54:06 
 * @author 蒋匿
 */
@Getter
@Setter
@Table(name="t_e_user_capital_account")
public class UserCapitalAccountEntity implements Serializable {
	
	private static final long serialVersionUID = -1L;
	
	/**
	 * 主键id
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	/**
	 * 机构编码
	 */
	private String oemCode;
	
	/**
	 * 用户id
	 */
	private Long userId;
	
	/**
	 * 用户类型 1-会员 2-城市合伙人 3-合伙人 4-平台 5-管理员 6-其他
	 */
	private Integer userType;
	
	/**
	 * 资金账号
	 */
	private String capitalAccount;
	
	/**
	 * 总金额
	 */
	private Long totalAmount;
	
	/**
	 * 可用金额
	 */
	private Long availableAmount;
	
	/**
	 * 冻结金额
	 */
	private Long blockAmount;
	/**
	 * 待结算金额
	 */
	private Long outstandingAmount;
	
	/**
	 * 状态 0-禁用 1-可用
	 */
	private Integer status;
	
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
