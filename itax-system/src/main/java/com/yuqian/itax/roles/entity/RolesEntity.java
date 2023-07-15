package com.yuqian.itax.roles.entity;

import java.io.Serializable;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;
/**
 * 角色管理
 * 
 * @Date: 2019年12月08日 20:58:54 
 * @author 蒋匿
 */
@Getter
@Setter
@Table(name="sys_e_roles")
public class RolesEntity implements Serializable {
	
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
	 * 机构编码
	 */
	private String oemName;
	/**
	 * 所属组织ID
	 */
	private Long orgId;
	/**
	 * 所属组织name
	 */
	private String orgName;
	
	/**
	 * 角色名称
	 */
	private String roleName;
	
	/**
	 * 角色编号
	 */
	private String roleCode;
	
	/**
	 * 类型 0：系统角色  不可删除  1：用户创建
	 */
	private Integer type;
	
	/**
	 * 状态 0-不可用 1-可用
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
