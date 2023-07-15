package com.yuqian.itax.user.entity;

import java.io.Serializable;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;
/**
 * 用户与城市的关系
 * 
 * @Date: 2019年12月08日 21:08:59 
 * @author 蒋匿
 */
@Getter
@Setter
@Table(name="sys_r_user_city")
public class UserCityRelaEntity implements Serializable {
	
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
	 * 省编码
	 */
	private String provinceCode;
	
	/**
	 * 市编码
	 */
	private String cityCode;
	
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
