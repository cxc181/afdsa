package com.yuqian.itax.user.entity;

import java.io.Serializable;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;
/**
 * 企业开票类目表
 * 
 * @Date: 2020年05月18日 10:18:18 
 * @author 蒋匿
 */
@Getter
@Setter
@Table(name="t_e_company_invoice_category")
public class CompanyInvoiceCategoryEntity implements Serializable {
	
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
	 * 订单号
	 */
	private String orderNo;
	
	/**
	 * 企业id
	 */
	private Long companyId;
	
	/**
	 * 行业id
	 */
	private Long industryId;
	
	/**
	 * 类目名称
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
	/**
	 *基础类目id
	 */
	private Long categoryBaseId;
	
}
