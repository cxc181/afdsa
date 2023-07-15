package com.yuqian.itax.snapshot.entity;

import java.io.Serializable;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;
/**
 * 用户资金快照
 * 
 * @Date: 2020年10月26日 11:25:02 
 * @author 蒋匿
 */
@Getter
@Setter
@Table(name="t_e_user_capital_snapshot")
public class UserCapitalSnapshotEntity implements Serializable {
	
	private static final long serialVersionUID = -1L;
	
	/**
	 * 主键id
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	/**
	 * 用户id
	 */
	private Long userId;
	
	/**
	 * 用户类型 1-会员 2 -系统用户
	 */
	private Integer userType;
	
	/**
	 * 机构编码
	 */
	private String oemCode;
	
	/**
	 * 消费钱包总余额(分)
	 */
	private Long totalAmountConsumption;
	
	/**
	 * 消费钱包可用余额(分)
	 */
	private Long availableAmountConsumption;
	
	/**
	 * 消费钱包冻结金额(分)
	 */
	private Long blockAmountConsumption;
	
	/**
	 * 消费钱包待结算金额(分)
	 */
	private Long outstandingAmountConsumption;
	
	/**
	 * 佣金钱包总余额(分)
	 */
	private Long totalAmountCommission;
	
	/**
	 * 佣金钱包可用余额(分)
	 */
	private Long availableAmountCommission;
	
	/**
	 * 佣金钱包冻结金额(分)
	 */
	private Long blockAmountCommission;
	
	/**
	 * 新增收益(分)
	 */
	private Long addRevenue;
	
	/**
	 * 快照时间
	 */
	private Date snapshotTime;
	
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
	
	
}
