package com.yuqian.itax.agent.entity;

import java.io.Serializable;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;
/**
 * oem机构开票类目关系表
 * 
 * @Date: 2020年12月25日 11:41:14 
 * @author 蒋匿
 */
@Getter
@Setter
@Table(name="t_r_oem_invoice_category")
public class OemInvoiceCategoryRelaEntity implements Serializable {
	
	private static final long serialVersionUID = -1L;
	
	/**
	 * 主键id
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	/**
	 * oem机构开票信息id
	 */
	private Long oemInvoiceInfoId;
	
	/**
	 * 机构编码
	 */
	private String oemCode;
	
	/**
	 * 类目库id
	 */
	private Long categoryBaseId;

	/**
	 * 类目库名称
	 */
	private String categoryName;
	
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
