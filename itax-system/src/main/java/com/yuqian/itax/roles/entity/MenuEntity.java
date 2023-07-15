package com.yuqian.itax.roles.entity;

import java.io.Serializable;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;
/**
 * 菜单信息
 * 
 * @Date: 2019年12月08日 20:58:38 
 * @author 蒋匿
 */
@Getter
@Setter
@Table(name="sys_e_menu")
public class MenuEntity implements Serializable {
	
	private static final long serialVersionUID = -1L;
	
	/**
	 * 主键id
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	/**
	 * 菜单名称
	 */
	private String name;
	
	/**
	 * 菜单URL
	 */
	private String url;
	
	/**
	 * 父菜单ID
	 */
	private Long parentId;
	
	/**
	 * 类型   0：目录   1：菜单   2：按钮
	 */
	private Integer type;
	
	/**
	 * 菜单图标
	 */
	private String icon;
	
	/**
	 * 菜单层级
	 */
	private String level;
	
	/**
	 * 排序
	 */
	private Integer orderNum;
	
	/**
	 * 状态 0 禁用 1 启用
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
