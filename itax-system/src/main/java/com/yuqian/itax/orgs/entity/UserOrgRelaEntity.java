package com.yuqian.itax.orgs.entity;

import java.io.Serializable;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;
/**
 * 用户组织关系表
 * 
 * @Date: 2019年12月08日 20:57:22 
 * @author 蒋匿
 */
@Getter
@Setter
@Table(name="sys_r_user_org")
public class UserOrgRelaEntity implements Serializable {
	
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
     * 组织类型  1-平台 2-机构 3-园区  4-高级合伙人 5-城市合伙人
     */
    private Integer orgType;
	
	/**
	 * 组织id
	 */
	private Long orgId;
	
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
