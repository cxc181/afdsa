package com.yuqian.itax.user.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;
/**
 * 人群标签
 * 
 * @Date: 2021年07月15日 15:48:57 
 * @author 蒋匿
 */
@Getter
@Setter
@Table(name="t_e_crowd_label")
public class CrowdLabelEntity implements Serializable {
	
	private static final long serialVersionUID = -1L;
	
	/**
	 * 主键id
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	/**
	 * 标签名称
	 */
	private String crowdLabelName;
	
	/**
	 * 机构编码
	 */
	private String oemCode;
	
	/**
	 * 状态 1-正常 2-作废
	 */
	private Integer status;
	
	/**
	 * 用户数
	 */
	private Integer memberUserNum;
	
	/**
	 * 标签描述
	 */
	private String crowdLabelDesc;
	
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

	/**
	 * 添加用户方式 1-列表导入 2-指定接入方
	 */
	private Integer addUserMode;

	/**
	 * 接入方id
	 */
	private Long accessPartyId;
}
