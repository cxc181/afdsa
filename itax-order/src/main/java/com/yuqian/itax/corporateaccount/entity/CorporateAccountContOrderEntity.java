package com.yuqian.itax.corporateaccount.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

/**
 * 对公户续费订单表
 * 
 * @Date: 2021年09月06日 16:10:35 
 * @author HZ
 */
@Getter
@Setter
@Table(name="t_e_corporate_account_cont_order")
public class CorporateAccountContOrderEntity implements Serializable {
	
	private static final long serialVersionUID = -1L;
	
	/**
	 * 
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	/**
	 * 订单号
	 */
	private String orderNo;
	
	/**
	 * 用户id
	 */
	private Long userId;
	
	/**
	 * 企业id
	 */
	private Long companyId;
	
	/**
	 * 对公户id
	 */
	private Long corporateAccountId;
	
	/**
	 * 对公户开户行
	 */
	private String corporateAccountBankName;
	
	/**
	 * 对公户银行账号
	 */
	private String corporateAccount;
	
	/**
	 * 订单金额（分）
	 */
	private Long orderAmount;
	
	/**
	 * 支付金额（分）
	 */
	private Long payAmount;
	
	/**
	 * 园区id
	 */
	private Long parkId;
	
	/**
	 * 机构编码
	 */
	private String oemCode;

	/**
	 * 过期时间
	 */
	private Date expirationTime;
	
	/**
	 * 创建时间
	 */
	private Date addTime;
	
	/**
	 * 创建人
	 */
	private String addUser;
	
	/**
	 * 更新时间
	 */
	private Date updateTime;
	
	/**
	 * 更新人
	 */
	private String updateUser;
	
	/**
	 * 备注
	 */
	private String remark;

}
