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
 * 经营范围税费分类编码表
 * 
 * @Date: 2021年11月09日 17:23:33 
 * @author 蒋匿
 */
@Getter
@Setter
@Table(name="t_e_businessscope_taxcode")
public class BusinessscopeTaxcodeEntity implements Serializable {
	
	private static final long serialVersionUID = -1L;
	
	/**
	 * 主键id
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	/**
	 * 经营范围
	 */
	private String businessScopName;
	
	/**
	 * 税收分类编码
	 */
	private String taxClassificationCode;
	
	/**
	 * 税费分类名称
	 */
	private String taxClassificationName;
	
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
