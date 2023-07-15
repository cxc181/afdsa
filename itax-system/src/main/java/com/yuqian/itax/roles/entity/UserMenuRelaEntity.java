package com.yuqian.itax.roles.entity;

import java.io.Serializable;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;
/**
 * 用户菜单关系表
 * 
 * @Date: 2020年04月20日 14:03:36 
 * @author 蒋匿
 */
@Getter
@Setter
@Table(name="sys_r_user_menu")
public class UserMenuRelaEntity implements Serializable {
	
	private static final long serialVersionUID = -1L;
	
	/**
	 * 主键id
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	/**
	 * 用户ID
	 */
	private Long userId;
	
	/**
	 * 机构编码
	 */
	private String oemCode;
	
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
