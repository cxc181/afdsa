package com.yuqian.itax.roles.entity;

import java.io.Serializable;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;
/**
 * 菜单对应的vue组件
 * 
 * @Date: 2019年12月08日 20:59:10 
 * @author 蒋匿
 */
@Getter
@Setter
@Table(name="sys_r_menu_vue")
public class MenuVueRelaEntity implements Serializable {
	
	private static final long serialVersionUID = -1L;
	
	/**
	 * 主键id
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	/**
	 * 菜单id
	 */
	private Long menuId;
	
	/**
	 * 路径
	 */
	private String path;
	
	/**
	 * vue组件名称
	 */
	private String component;
	
	/**
	 * 重定向地址
	 */
	private String redirect;
	
	/**
	 * 排序
	 */
	private Integer orderNum;
	
	/**
	 * 名称
	 */
	private String name;
	
	/**
	 * 是否隐藏 0 隐藏 1不隐藏
	 */
	private Integer hidden;
	
	/**
	 * 样式
	 */
	private String meta;
	
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
