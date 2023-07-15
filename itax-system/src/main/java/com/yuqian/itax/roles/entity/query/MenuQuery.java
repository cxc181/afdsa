package com.yuqian.itax.roles.entity.query;

import com.yuqian.itax.common.base.entity.query.BaseQuery;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class MenuQuery extends BaseQuery {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3117013156113006743L;
	/**
	 * 父菜单ID
	 */
	private Long id;
	
}
