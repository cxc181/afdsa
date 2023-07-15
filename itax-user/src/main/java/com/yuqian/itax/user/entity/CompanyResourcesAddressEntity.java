package com.yuqian.itax.user.entity;

import java.io.Serializable;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;
/**
 * 企业资源所在地管理
 * 
 * @Date: 2019年12月14日 13:56:31 
 * @author 蒋匿
 */
@Getter
@Setter
@Table(name="t_e_company_resources_address")
public class CompanyResourcesAddressEntity implements Serializable {
	
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
	 * 资源类型  1-公章 2-财务章 3-对公账号u盾 4-营业执照 
	 */
	private Integer resourcesType;
	
	/**
	 * 所在地
	 */
	private String address;

	/**
	 * 机构编码
	 */
	private String oemCode;

	/**
	 * 是否在园区 0-不在园区 1-在园区
	 */
	private Integer isInPark;

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
