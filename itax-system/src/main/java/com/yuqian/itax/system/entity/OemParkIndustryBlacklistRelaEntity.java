package com.yuqian.itax.system.entity;

import java.io.Serializable;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;
/**
 * oem机构园区行业黑名单
 * 
 * @Date: 2020年08月07日 10:38:39 
 * @author 蒋匿
 */
@Getter
@Setter
@Table(name="t_r_oem_park_industry_blacklist")
public class OemParkIndustryBlacklistRelaEntity implements Serializable {
	
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
	 * 园区id
	 */
	private Long parkId;
	
	/**
	 * 园区编码
	 */
	private String parkCode;
	
	/**
	 * 行业id
	 */
	private Long industryId;
	
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
