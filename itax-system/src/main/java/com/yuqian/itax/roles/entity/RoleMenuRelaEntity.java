package com.yuqian.itax.roles.entity;

import java.io.Serializable;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;
/**
 * 角色对应菜单
 * 
 * @Date: 2019年12月08日 20:59:24 
 * @author 蒋匿
 */
@Getter
@Setter
@Table(name="sys_r_role_menu")
public class RoleMenuRelaEntity implements Serializable {
	
	private static final long serialVersionUID = -1L;
	
	/**
	 * 主键id
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	/**
	 * 角色ID
	 */
	private Long roleId;
	
	/**
	 * 菜单ID
	 */
	private Long menuId;
	
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
