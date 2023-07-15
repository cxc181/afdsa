package com.yuqian.itax.system.entity;

import java.io.Serializable;
import java.util.Date;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;
/**
 * 开票类目
 * 
 * @Date: 2019年12月08日 20:37:55 
 * @author yejian
 */
@Getter
@Setter
@Table(name="t_e_invoice_category")
public class InvoiceCategoryEntity implements Serializable {
	
	private static final long serialVersionUID = -1L;
	
	/**
	 * 主键id
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@ApiModelProperty(value = "主键id")
	private Long id;
	
	/**
	 * 行业id
	 */
	@ApiModelProperty(value = "行业id")
	private Long industryId;
	
	/**
	 * 类目名称
	 */
	@ApiModelProperty(value = "类目名称")
	private String categoryName;
	
	/**
	 * 添加时间
	 */
	@ApiModelProperty(value = "添加时间")
	private Date addTime;
	
	/**
	 * 添加人
	 */
	@ApiModelProperty(value = "添加人")
	private String addUser;
	
	/**
	 * 修改时间
	 */
	@ApiModelProperty(value = "修改时间")
	private Date updateTime;
	
	/**
	 * 修改人
	 */
	@ApiModelProperty(value = "修改人")
	private String updateUser;
	
	/**
	 * 备注
	 */
	@ApiModelProperty(value = "备注")
	private String remark;
	/**
	 *基础类目id
	 */
	private Long categoryBaseId;
	/**
	 *园区id
	 */
	private Long parkId;
	
}
