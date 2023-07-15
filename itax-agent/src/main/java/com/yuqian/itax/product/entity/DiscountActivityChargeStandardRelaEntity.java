package com.yuqian.itax.product.entity;

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
 * 特价活动开票服务费标准
 * 
 * @Date: 2021年07月15日 15:47:32 
 * @author 蒋匿
 */
@Getter
@Setter
@Table(name="t_r_discount_activity_charge_standard")
public class DiscountActivityChargeStandardRelaEntity implements Serializable {
	
	private static final long serialVersionUID = -1L;
	
	/**
	 * 主键id
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	/**
	 * 费用类型 1-服务费 
	 */
	private Integer chargeType;
	
	/**
	 * 费用金额最小(分)
	 */
	private Long chargeMin;
	
	/**
	 * 费用金额最大(分)
	 */
	private Long chargeMax;
	
	/**
	 * 收费比率(百分比)
	 */
	private BigDecimal chargeRate;
	
	/**
	 * 机构编码
	 */
	private String oemCode;
	
	/**
	 * 特价活动id
	 */
	private Long discountActivityId;
	
	/**
	 * 排序字段
	 */
	private Integer orderSn;
	
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
