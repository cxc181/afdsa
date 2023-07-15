package com.yuqian.itax.common.base.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 当前用户信息
 *
 * @date: 2017年7月10日 下午6:09:03 
 * @author LiuXianTing
 */
@Data
public class CurrUser implements Serializable {

	private static final long serialVersionUID = 1L;
	
	/**
	 * 用户id
	 */
	private Long userId;

	/**
	 * 用户账号
	 */
	private String useraccount;
	
	/**
	 * 公司code
	 */
	private String oemCode;

	/**
	 * 团队id
	 */
	private Integer groupId;

	/**
	 * 用户类型：0-超级管理员，1-系统管理员，9-普通用户
	 */
	private String usertype;

	/**
	 * 角色id
	 */
	private List<Long> roleIds;

	/**
	 * 用户权限
	 */
	private Set<String> permissions = new HashSet<>();
	
	public CurrUser() {
		super();
	}
	
	public CurrUser(Long userId, String useraccount, String oemCode, Integer groupId) {
		super();
		this.userId = userId;
		this.useraccount = useraccount;
		this.oemCode = oemCode;
		this.groupId = groupId;
	}
}
