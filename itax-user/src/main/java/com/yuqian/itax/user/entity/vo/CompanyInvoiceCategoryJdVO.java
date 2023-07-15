package com.yuqian.itax.user.entity.vo;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

/**
 * 企业开票类目表
 * 
 * @Date: 2020年05月18日 10:18:18 
 * @author 蒋匿
 */
@Getter
@Setter
@Table(name="t_e_company_invoice_category")
public class CompanyInvoiceCategoryJdVO implements Serializable {
	
	private static final long serialVersionUID = -1L;
	

	/**
	 * 类目名称
	 */
	private String categoryName;
	
	/**
	 *基础类目id
	 */
	private Long categoryBaseId;
	
}
