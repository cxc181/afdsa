package com.yuqian.itax.park.entity;

import java.io.Serializable;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;
/**
 * 园区经办人账号表
 * 
 * @Date: 2020年03月04日 09:44:53 
 * @author 蒋匿
 */
@Getter
@Setter
@Table(name="t_e_park_agent_account")
public class ParkAgentAccountEntity implements Serializable {
	
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
	 * 经办人姓名
	 */
	private String agentName;
	
	/**
	 * 经办人账号
	 */
	private String agentAccount;

	/**
	 * 身份证号码
	 */
	private String idCardNo;

	/**
	 * 身份证正面照地址
	 */
	private String idCardFront;

	/**
	 * 身份证反面照地址
	 */
	private String idCardBack;

	/**
	 * 状态 0-禁用 1-正常  2-已删除
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
