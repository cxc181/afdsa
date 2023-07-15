package com.yuqian.itax.system.entity;

import java.io.Serializable;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;
/**
 * 市
 * 
 * @Date: 2019年12月08日 20:33:09 
 * @author 蒋匿
 */
@Getter
@Setter
@Table(name="sys_e_city")
public class CityEntity implements Serializable {
	
	private static final long serialVersionUID = -1L;
	
	/**
	 * 主键id
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	/**
	 * 省编号
	 */
	private String provinceCode;
	
	/**
	 * 市编号
	 */
	private String code;
	
	/**
	 * 市名称
	 */
	private String name;
	
	/**
	 * 是否可用 1-可用 0-不可用
	 */
	private Integer isUse;
	
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
