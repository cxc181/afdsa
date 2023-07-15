package com.yuqian.itax.system.entity;

import java.io.Serializable;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;
/**
 * 银行信息
 * 
 * @Date: 2019年12月14日 11:35:19 
 * @author 蒋匿
 */
@Getter
@Setter
@Table(name="sys_e_bank_info")
public class BankInfoEntity implements Serializable {
	
	private static final long serialVersionUID = -1L;
	
	/**
	 * 主键id
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	/**
	 * 银行编号
	 */
	private String bankCode;
	
	/**
	 * 银行名称
	 */
	private String bankName;
	
	/**
	 * 银行简称
	 */
	private String bankAbbreviation;
	
	/**
	 * logo图片地址
	 */
	private String bankLogoUrl;
	
	/**
	 * 背景图
	 */
	private String backgroundImage;

	/**
	 * 储蓄卡单笔限额
	 */
	private Integer singleLimitCash;

	/**
	 * 储蓄卡单日限额
	 */
	private Integer dailyLimitCash;

	/**
	 * 信用卡单笔限额
	 */
	private Integer singleLimitCredit;

	/**
	 * 信用卡单日限额
	 */
	private Integer dailyLimitCredit;

	/**
	 * 状态  1上架，2下架
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
