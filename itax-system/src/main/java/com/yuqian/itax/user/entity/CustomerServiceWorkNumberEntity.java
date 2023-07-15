package com.yuqian.itax.user.entity;

import java.io.Serializable;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;
/**
 * 坐席客服工号表
 * 
 * @Date: 2019年12月12日 15:14:27 
 * @author 蒋匿
 */
@Getter
@Setter
@Table(name="t_e_customer_service_work_number")
public class CustomerServiceWorkNumberEntity implements Serializable {
	
	private static final long serialVersionUID = -1L;
	
	/**
	 * 主键id
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	/**
	 * 坐席id 对于系统用户表用户类型为客服 的用户id
	 */
	private Long userId;
	
	/**
	 * 工号
	 */
	private String workNumber;
	
	/**
	 * 工号密码 md5加密
	 */
	private String workNumberPwd;
	
	/**
	 * slat
	 */
	private String slat;
	
	/**
	 * 工号姓名 显示名称
	 */
	private String workNumberName;
	
	/**
	 * 状态 0-禁用 1-正常
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
