package com.yuqian.itax.user.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;
/**
 * 代理商分润规则
 * 
 * @Date: 2019年12月12日 17:12:52 
 * @author 蒋匿
 */
@Getter
@Setter
@Table(name="t_e_agent_profits_rules")
public class AgentProfitsRulesEntity implements Serializable {
	
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
	 *  代理商类型  0-平台 1-高级合伙人 2-城市合伙人
	 */
	private Integer agentType;
	
	/**
	 * 代理商id  对应系统用户表id
	 */
	private Long agentId;
	
	/**
	 * 代理商账号
	 */
	private String agentAccount;
	
	/**
	 * 会费分润率
	 */
	private BigDecimal membershipFee;
	
	/**
	 * 开户分润率
	 */
	private BigDecimal registerFee;
	
	/**
	 * 开票分润率
	 */
	private BigDecimal invoiceFee;

	/**
	 * 企业注销服务费分润率
	 */
	private BigDecimal cancelCompanyFee;
	
	/**
	 * 状态 0-不可用 1-可用
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
