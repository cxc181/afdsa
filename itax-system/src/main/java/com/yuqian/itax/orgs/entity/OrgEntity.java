package com.yuqian.itax.orgs.entity;

import java.io.Serializable;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;
/**
 * 组织管理
 * 
 * @Date: 2019年12月08日 20:57:08 
 * @author 蒋匿
 */
@Getter
@Setter
@Table(name="sys_e_org")
public class OrgEntity implements Serializable {
	
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
	 * 组织名称
	 */
	private String orgName;
	
	/**
	 * 上级组织id
	 */
	private Long parentOrgId;
	
	/**
	 * 组织树
	 */
	private String orgTree;

	/**
	 * 组织类型  1-平台 2-机构 3-园区  4-高级合伙人 5-城市合伙人
	 */
	private Integer orgType;
	
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
