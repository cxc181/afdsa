package com.yuqian.itax.user.entity;

import java.io.Serializable;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;
/**
 * 系统用户
 * 
 * @Date: 2019年12月08日 20:51:44 
 * @author 蒋匿
 */
@Getter
@Setter
@Table(name="sys_e_user")
public class UserEntity implements Serializable {
	
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
	 * 密码
	 */
	private String password;
	
	/**
	 * slat
	 */
	private String slat;
	
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
	 * 绑定城市服务商
	 */
	private String bindingAccount;

	/**
	 * 账号类型  1-管理员  2-坐席客服 3-普通用户
	 */
	private Integer accountType;


	/**
	 * 园区id
	 */
	private Long parkId;

	/**
	 * 平台类型  1-平台 2-机构 3-园区 4-城市合伙人 5-城市合伙人
	 */
	private Integer platformType;
	
	/**
	 * 支付密码
	 */
	private String payPassword;
	
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

	/**
	 * 用户名称
	 */
	private String nickname;
}
