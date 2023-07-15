package com.yuqian.itax.agent.entity;

import java.io.Serializable;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;
/**
 * 机构与园区的关系
 * 
 * @Date: 2019年12月07日 20:35:24 
 * @author 蒋匿
 */
@Getter
@Setter
@Table(name="t_r_oem_park")
public class OemParkRelaEntity implements Serializable {
	
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
	 * 园区id
	 */
	private Long parkId;

	/**
	 * 机构专属协议模板id
	 */
	private Long agreementTemplateId;
	
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
