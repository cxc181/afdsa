package com.yuqian.itax.system.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;
/**
 * 行业基础信息表
 * 
 * @Date: 2022年09月26日 10:50:57 
 * @author 蒋匿
 */
@Getter
@Setter
@Table(name="t_e_industry_base")
public class IndustryBaseEntity implements Serializable {
	
	private static final long serialVersionUID = -1L;
	
	/**
	 * 主键id
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	/**
	 * 行业名称
	 */
	private String industryName;
	
	/**
	 * 增值税率数组，为百分比数据
	 */
	private String vatRate;
	
	/**
	 * 状态 1-正常 2-下架
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
	
	
}
