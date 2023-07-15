package com.yuqian.itax.park.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;
/**
 * 园区禁用字
 * 
 * @Date: 2021年10月19日 14:31:08 
 * @author 蒋匿
 */
@Getter
@Setter
@Table(name="t_e_park_disable_word")
public class ParkDisableWordEntity implements Serializable {
	
	private static final long serialVersionUID = -1L;
	
	/**
	 * 主键id
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	/**
	 * 园区id
	 */
	private Long parkId;
	
	/**
	 * 禁用字
	 */
	private String disableWord;
	
	/**
	 * 添加人
	 */
	private String addUser;
	
	/**
	 * 添加时间
	 */
	private Date addTime;
	
	/**
	 * 修改人
	 */
	private String updateUser;
	
	/**
	 * 修改时间
	 */
	private Date updateTime;
	
	/**
	 * 备注
	 */
	private String remark;
	
	
}
