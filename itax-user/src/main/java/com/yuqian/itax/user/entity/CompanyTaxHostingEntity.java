package com.yuqian.itax.user.entity;

import java.io.Serializable;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;
/**
 * 企业税务托管表
 * 
 * @Date: 2020年12月25日 11:38:54 
 * @author 蒋匿
 */
@Getter
@Setter
@Table(name="t_e_company_tax_hosting")
public class CompanyTaxHostingEntity implements Serializable {
	
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
	 * 税务盘类型 1-ukey 2-税控盘
	 */
	private Integer taxDiscType;
	/**
	 * 票面金额类型 1-1w 2-10w 3-100w
	 */
	private Integer faceAmountType;
	/**
	 * 票面金额(分)
	 */
	private Long faceAmount;
	/**
	 * 税务盘编号
	 */
	private String taxDiscCode;
	
	/**
	 * 通道方 1-百旺
	 */
	private Integer channel;
	
	/**
	 * 托管状态 0-未托管 1-已托管
	 */
	private Integer status;
	
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
