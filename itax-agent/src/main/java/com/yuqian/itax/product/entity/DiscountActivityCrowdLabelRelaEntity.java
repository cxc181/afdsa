package com.yuqian.itax.product.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;
/**
 * 产品特价活动人群关系表
 * 
 * @Date: 2021年07月15日 15:46:59 
 * @author 蒋匿
 */
@Getter
@Setter
@Table(name="t_r_discount_activity_crowd_label")
public class DiscountActivityCrowdLabelRelaEntity implements Serializable {
	
	private static final long serialVersionUID = -1L;
	
	/**
	 * 主键id
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	/**
	 * 特价活动id
	 */
	private Long discountActivityId;
	
	/**
	 * 人群id
	 */
	private Long crowdLabelId;
	
	/**
	 * 机构编码
	 */
	private String oemCode;
	
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
