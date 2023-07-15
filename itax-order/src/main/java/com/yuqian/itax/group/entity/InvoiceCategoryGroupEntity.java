package com.yuqian.itax.group.entity;

import java.io.Serializable;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;
/**
 * 集团类目表
 * 
 * @Date: 2020年03月04日 09:25:37 
 * @author 蒋匿
 */
@Getter
@Setter
@Table(name="t_e_invoice_category_group")
public class InvoiceCategoryGroupEntity implements Serializable {
	
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
	 * 行业id
	 */
	private Long industryId;

	/**
	 *  基础类目ID
	 */
	private Long categoryBaseId;
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
	
	
}
