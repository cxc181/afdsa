package com.yuqian.itax.user.entity;

import java.io.Serializable;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;
/**
 * 用户扩展表
 * 
 * @Date: 2019年12月08日 20:51:30 
 * @author 蒋匿
 */
@Getter
@Setter
@Table(name="sys_e_user_extend")
public class UserExtendEntity implements Serializable {
	
	private static final long serialVersionUID = -1L;
	
	/**
	 * 主键id
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	/**
	 * 用户id
	 */
	private Long userId;
	
//	/**
//	 * 用户类型编码
//	 */
//	private String userTypeCode;
	
	/**
	 * 上级用户id
	 */
	private Long parentUserId;
	
	/**
	 * 联系电话
	 */
	private String phone;
	
	/**
	 * 联系微信
	 */
	private String wechat;
	
	/**
	 * 联系邮箱
	 */
	private String email;
	
	/**
	 * 层级树
	 */
	private String userTree;
	
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
