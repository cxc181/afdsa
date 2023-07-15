package com.yuqian.itax.agent.entity;

import java.io.Serializable;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;
/**
 * oem机构参数配置表
 * 
 * @Date: 2020年07月21日 11:19:36 
 * @author 蒋匿
 */
@Getter
@Setter
@Table(name="t_e_oem_config")
public class OemConfigEntity implements Serializable {
	
	private static final long serialVersionUID = -1L;
	
	/**
	 * 主键id
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	/**
	 * 机构编码
	 */
	private String oemCode;
	
	/**
	 * 参数编码
	 */
	private String paramsCode;
	
	/**
	 * 参数值
	 */
	private String paramsValue;
	
	/**
	 * 上级参数编码
	 */
	private String parentParamsCode;

	/**
	 * 参数描述
	 */
	private String paramsDesc;
	
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
