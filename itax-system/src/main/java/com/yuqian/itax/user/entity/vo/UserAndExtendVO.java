package com.yuqian.itax.user.entity.vo;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

/**
 * 系统用户
 * 
 * @Date: 2019年12月08日 20:51:44 
 * @author 蒋匿
 */
@Getter
@Setter
public class UserAndExtendVO implements Serializable {
	
	private static final long serialVersionUID = -1L;
	
	/**
	 * 主键id
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
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
	 * 状态 0-禁用 1-可用
	 */
	private Integer status;
	
	/**
	 * 电话
	 */
	private String phone;

	/**
	 * 用户名称
	 */
	private String nickname;
}
