package com.yuqian.itax.roles.entity;

import java.io.Serializable;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;
/**
 * 用户与角色的关系表
 * 
 * @Date: 2019年12月08日 20:59:38 
 * @author 蒋匿
 */
@Getter
@Setter
@Table(name="sys_r_user_role")
public class UserRoleRelaEntity implements Serializable {
	
	private static final long serialVersionUID = -1L;
	
	/**
	 * 主键id
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	/**
	 * 用户Id
	 */
	private Long userId;
	
	/**
	 * 角色Id
	 */
	private Long roleId;
	
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
