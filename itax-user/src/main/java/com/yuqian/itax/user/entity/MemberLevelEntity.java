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
 * 会员等级管理
 * 
 * @Date: 2019年12月07日 20:48:00 
 * @author 蒋匿
 */
@Getter
@Setter
@Table(name="t_e_member_level")
public class MemberLevelEntity implements Serializable {
	
	private static final long serialVersionUID = -1L;
	
	/**
	 * 主键id
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	/**
	 * 等级名称
	 */
	private String levelName;

    /**
     * 等级标识 -1-员工 0-普通会员 1-VIP 3-税务顾问 5-城市服务商
     */
	private Integer levelNo;
	
	/**
	 * 机构编码
	 */
	private String oemCode;

	/**
	 * 个体注册数
	 */
	private Integer registCompanyNum;

	/**
	 * 开票最低金额
	 */
	private Long invoiceMinMoney;

	/**
	 * 开票达标个体数
	 */
	private Integer completeInvoiceCompanyNum;

	/**
	 * 是否支付升级 0-否 1-是
	 */
	private Integer isPayUpgrade;

	/**
	 * 支付金额
	 */
	private Long payMoney;
	
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
