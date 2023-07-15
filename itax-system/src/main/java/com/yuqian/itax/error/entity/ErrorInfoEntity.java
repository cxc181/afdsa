package com.yuqian.itax.error.entity;

import java.io.Serializable;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;
/**
 * 错误信息
 * 
 * @Date: 2019年12月08日 20:41:20 
 * @author 蒋匿
 */
@Getter
@Setter
@Table(name="sys_e_error_info")
public class ErrorInfoEntity implements Serializable {
	
	private static final long serialVersionUID = -1L;
	
	/**
	 * 主键id
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	/**
	 * 错误编码
	 */
	private String errorCode;
	
	/**
	 * 客户端类型 1-接口  2-后台
	 */
	private Integer clientType;
	
	/**
	 * 模块名称
	 */
	private String modelName;
	
	/**
	 * 类名称
	 */
	private String className;
	
	/**
	 * 错误信息
	 */
	private String errorContent;
	
	/**
	 * 错误参数信息
	 */
	private String errorParamsInfo;
	
	/**
	 * 机构编码
	 */
	private String oemCode;
	
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
