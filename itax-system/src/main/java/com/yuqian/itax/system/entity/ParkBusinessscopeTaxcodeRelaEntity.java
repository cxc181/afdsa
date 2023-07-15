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
 * 园区经营范围与经营范围基础库关系表
 * 
 * @Date: 2022年12月29日 13:53:08 
 * @author 蒋匿
 */
@Getter
@Setter
@Table(name="t_r_park_businessscope_taxcode")
public class ParkBusinessscopeTaxcodeRelaEntity implements Serializable {
	
	private static final long serialVersionUID = -1L;
	
	/**
	 * 主键id
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	/**
	 * 园区经营范围id
	 */
	private Long parkBusinessscopeId;
	
	/**
	 * 经营范围基础库id
	 */
	private Long businessscopeBaseId;
	
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
