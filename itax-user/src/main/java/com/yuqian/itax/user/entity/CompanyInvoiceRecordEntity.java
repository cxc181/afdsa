package com.yuqian.itax.user.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;
/**
 * 企业开票记录
 * 
 * @Date: 2019年12月10日 11:35:24 
 * @author 蒋匿
 */
@Getter
@Setter
@Table(name="t_e_company_invoice_record")
public class CompanyInvoiceRecordEntity implements Serializable {
	
	private static final long serialVersionUID = -1L;
	
	/**
	 * 主键id
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	/**
	 * 企业id
	 */
	private Long companyId;
	
	/**
	 * 年份
	 */
	private String year;
	
	/**
	 * 年度总开票金额
	 */
	private Long totalInvoiceAmount;
	
	/**
	 * 年度已开票金额
	 */
	private Long useInvoiceAmount;
	
	/**
	 * 年度可开票金额
	 */
	private Long remainInvoiceAmount;

	/**
	 * 有效时间
	 */
	private Date endTime;

	/**
	 * 机构编码
	 */
	private String oemCode;
	
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
