package com.yuqian.itax.agent.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;
/**
 * 接入方信息表
 * 
 * @Date: 2021年08月04日 17:18:19 
 * @author 蒋匿
 */
@Getter
@Setter
@Table(name="t_e_oem_access_party")
public class OemAccessPartyEntity implements Serializable {
	
	private static final long serialVersionUID = -1L;
	
	/**
	 * 主键id
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	/**
	 * 接入方名称
	 */
	private String accessPartyName;
	
	/**
	 * 接入方编号
	 */
	private String accessPartyCode;
	
	/**
	 * 所属oem机构
	 */
	private String oemCode;
	
	/**
	 * 秘钥
	 */
	private String accessPartySecret;
	
	/**
	 * 状态  1-上架 2-下级
	 */
	private Integer status;

	/**
	 * 登录超时时间
	 */
	private Long loginTime;

	/**
	 * 跳转小程序地址
	 */
	private String appletAddress;

	/**
	 * 回调地址
	 */
	private String callbackUrl;
	
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
