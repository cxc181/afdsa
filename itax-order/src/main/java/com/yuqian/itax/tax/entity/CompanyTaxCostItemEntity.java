package com.yuqian.itax.tax.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
/**
 * 企业税单成本项表
 * 
 * @Date: 2021年12月13日 20:50:26 
 * @author 蒋匿
 */
@Getter
@Setter
@Table(name="t_e_company_tax_cost_item")
public class CompanyTaxCostItemEntity implements Serializable {
	
	private static final long serialVersionUID = -1L;
	
	/**
	 * 主键id
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	/**
	 * 企业税单id
	 */
	private Long companyTaxId;
	
	/**
	 * 行业id
	 */
	private Long industryId;
	
	/**
	 * 成本项名称
	 */
	private String costItemName;
	
	/**
	 * 成本项比例
	 */
	private BigDecimal costItemRate;
	
	/**
	 * 成本项金额（分）
	 */
	private Long costItemAmount;
	
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
