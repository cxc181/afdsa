package com.yuqian.itax.snapshot.entity;

import java.io.Serializable;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;
/**
 * 系统用户快照
 * 
 * @Date: 2020年10月26日 11:25:11 
 * @author 蒋匿
 */
@Getter
@Setter
@Table(name="sys_e_user_snapshot")
public class UserSnapshotEntity implements Serializable {
	
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
	 * 机构编码
	 */
	private String oemCode;
	
	/**
	 * 用户类型 1-平台 2-机构 3-园区 4-高级城市合伙人 5-城市合伙人
	 */
	private Integer userType;
	
	/**
	 * 注册账号
	 */
	private String username;
	
	/**
	 * 绑定手机号
	 */
	private String bindingAccount;
	
	/**
	 * 状态 0-禁用 1-可用 2-注销
	 */
	private Integer status;
	
	/**
	 * 注册时间
	 */
	private Date registTime;
	
	/**
	 * 快照时间
	 */
	private Date snapshotTime;
	
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
