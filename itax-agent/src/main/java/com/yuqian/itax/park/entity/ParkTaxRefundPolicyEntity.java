package com.yuqian.itax.park.entity;

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
 * 园区返税政策
 * 
 * @Date: 2022年09月26日 10:51:28 
 * @author 蒋匿
 */
@Getter
@Setter
@Table(name="t_e_park_tax_refund_policy")
public class ParkTaxRefundPolicyEntity implements Serializable {
	
	private static final long serialVersionUID = -1L;
	
	/**
	 * 主键id
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	/**
	 * 园区id
	 */
	private Long parkId;
	
	/**
	 * 税费最小值（分）
	 */
	private Long minValue;
	
	/**
	 * 税费最大值（分）
	 */
	private Long maxValue;
	
	/**
	 * 奖励比例，小数
	 */
	private BigDecimal rewardRate;
	
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
