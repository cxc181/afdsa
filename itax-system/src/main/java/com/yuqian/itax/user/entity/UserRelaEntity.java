package com.yuqian.itax.user.entity;

import java.io.Serializable;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;
/**
 * 用户关系表
 * 
 * @Date: 2019年12月18日 14:04:55 
 * @author 蒋匿
 */
@Getter
@Setter
@Table(name="t_e_user_rela")
public class UserRelaEntity implements Serializable {
	
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
	
	/**
	 * 用户层级  1-平台 2-机构、园区 3-高级合伙人 4-城市合伙人 5-会员 6-机构用户 7-园区用户
	 * 1/2/3/4/5/6/7
	 */
	private Integer userClass;
	
	/**
	 * 用户树 用户id组成的树
	 */
	private String userTree;
	
	/**
	 * 上级用户id
	 */
	private Long parentUserId;
	
	/**
	 * 用户层级  1-平台 2-机构、园区 3-高级合伙人 4-城市合伙人 5-会员 6-机构用户 7-园区用户
	 */
	private Integer parentUserType;
	
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
