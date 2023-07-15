package com.yuqian.itax.corporateaccount.entity;

import java.io.Serializable;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;
/**
 *  对公户提现订单表
 * 
 * @Date: 2020年09月07日 09:12:30 
 * @author 蒋匿
 */
@Getter
@Setter
@Table(name="t_e_corporate_account_withdrawal_order")
public class CorporateAccountWithdrawalOrderEntity implements Serializable {
	
	private static final long serialVersionUID = -1L;
	
	/**
	 * 主键id
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	/**
	 * 订单号
	 */
	private String orderNo;
	
	/**
	 * 对公户id
	 */
	private Long corporateAccountId;
	
	/**
	 * 园区id
	 */
	private Long parkId;
	
	/**
	 * 提现金额(分)
	 */
	private Long withdrawalAmount;
	
	/**
	 * 到账银行账户
	 */
	private String arriveBankAccount;
	
	/**
	 * 到账银行名称
	 */
	private String arriveBankName;
	
	/**
	 * 到账人姓名
	 */
	private String arriveUserName;
	
	/**
	 * 到账时间
	 */
	private Date arriveTime;
	
	/**
	 * 开票订单编号
	 */
	private String invoiceOrderNo;
	
	/**
	 * 收款记录id(多个id之间用逗号分割)
	 */
	private String collectionRecordIds;
	
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
