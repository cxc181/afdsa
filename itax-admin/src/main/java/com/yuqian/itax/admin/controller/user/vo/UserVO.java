package com.yuqian.itax.admin.controller.user.vo;

import com.yuqian.itax.user.entity.UserEntity;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 系统用户
 * 
 * @Date: 2019年12月08日 20:51:44 
 * @author 蒋匿
 */
@Getter
@Setter
public class UserVO implements Serializable {
	
	private static final long serialVersionUID = -1L;
	
	/**
	 * 账号
	 */
	private String username;
	
	/**
	 * 机构编码
	 */
	private String oemCode;

	/**
	 * 机构名称
	 */
	private String oemName;

	/**
	 * 用户名称
	 */
	private String nickname;

	public UserVO() {

	}

	public UserVO(UserEntity userEntity) {
		this.username = userEntity.getUsername();
		this.oemCode = userEntity.getOemCode();
		this.oemName = userEntity.getOemName();
		this.nickname = userEntity.getNickname();
	}
}
