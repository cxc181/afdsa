package com.yuqian.itax.system.entity;

import java.io.Serializable;
import java.util.Date;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;
/**
 * 物流公司信息
 * 
 * @Date: 2020年02月13日 13:40:54 
 * @author 蒋匿
 */
@Getter
@Setter
@Table(name="sys_e_logis_company")
public class LogisCompanyEntity implements Serializable {
	
	private static final long serialVersionUID = -1L;
	
	/**
	 * 主键id
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@ApiModelProperty(value = "主键id")
	private Integer id;
	
	/**
	 * 快递公司编码
	 */
	@ApiModelProperty(value = "快递公司编码")
	private String companyCode;
	
	/**
	 * 快递公司名称
	 */
	@ApiModelProperty(value = "快递公司名称")
	private String companyName;
	
	/**
	 * 备注信息
	 */
	@ApiModelProperty(value = "备注信息")
	private String remark;
	
	/**
	 * 添加时间
	 */
	@ApiModelProperty(value = "添加时间")
	private Date addDate;
	
	/**
	 * 更新时间
	 */
	@ApiModelProperty(value = "更新时间")
	private Date updateDate;
	
	
}
