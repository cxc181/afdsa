package com.yuqian.itax.system.entity;

import java.io.Serializable;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;
import org.jeecgframework.poi.excel.annotation.Excel;

import javax.persistence.*;
/**
 * 基础开票类目库
 * 
 * @Date: 2020年12月25日 11:38:14 
 * @author 蒋匿
 */
@Getter
@Setter
@Table(name="t_e_invoice_category_base")
public class InvoiceCategoryBaseEntity implements Serializable {
	
	private static final long serialVersionUID = -1L;
	
	/**
	 * 主键id
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	/**
	 * 税收分类编码
	 */
	private String taxClassificationCode;
	
	/**
	 * 税收分类名称
	 */
	private String taxClassificationName;
	
	/**
	 * 税收分类简称
	 */
	private String taxClassificationAbbreviation;
	
	/**
	 * 商品名称
	 */
	private String goodsName;
	
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
