package com.yuqian.itax.system.entity;

import java.io.Serializable;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;
/**
 * 操作日志
 * 
 * @Date: 2019年12月08日 20:36:31 
 * @author 蒋匿
 */
@Getter
@Setter
@Table(name="sys_e_operation_log")
public class OperationLogEntity implements Serializable {
	
	private static final long serialVersionUID = -1L;
	
	/**
	 * 主键id
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	/**
	 * 操作时间
	 */
	private Date oprTime;
	
	/**
	 * 客户端类型 1-接口 2-后台
	 */
	private Integer oprType;
	
	/**
	 * 操作人
	 */
	private Long oprUserId;
	
	/**
	 * 操作IP地址
	 */
	private String ipAddr;
	
	/**
	 * 操作模块
	 */
	private String module;
	
	/**
	 * 操作描述
	 */
	private String oprDesc;
	
	/**
	 * 操作类名
	 */
	private String className;
	
	/**
	 * 操作方法名
	 */
	private String method;
	
	/**
	 * 操作结果 1 成功 0失败
	 */
	private Integer result;
	
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
