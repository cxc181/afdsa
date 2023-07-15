package com.yuqian.itax.roles.entity.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

/**
 * 菜单树
 * @author Karen
 *
 */
@Getter
@Setter
@ToString
public class MenuTreeVO implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3577243811639686258L;

	private Long id; //菜单ID
	/**
	 * 父菜单ID，一级菜单为0
	 */
	private Long parentId;

	/**
	 * 菜单名称
	 */
	private String name;
	/**
	 * 菜单图标
	 */
	private String icon;
	/**
	 * 排序
	 */
	private Integer orderNum;
	/**
	 * 子菜单
	 */
	private List<MenuTreeVO> children;
}
