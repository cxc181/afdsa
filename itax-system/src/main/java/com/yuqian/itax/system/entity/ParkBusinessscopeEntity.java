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
 * 园区经营范围表
 * 
 * @Date: 2022年12月29日 13:52:58 
 * @author 蒋匿
 */
@Getter
@Setter
@Table(name="t_e_park_businessscope")
public class ParkBusinessscopeEntity implements Serializable {
	
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
	 * 经营范围名称
	 */
	private String businessscopeName;
	
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
	 * 是否删除
	 */
	private Integer isDelete;
}
