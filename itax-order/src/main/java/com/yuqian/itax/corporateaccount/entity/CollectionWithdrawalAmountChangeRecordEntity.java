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
 * 对公户银行收款核销记录表
 * 
 * @Date: 2020年09月07日 17:48:04 
 * @author 蒋匿
 */
@Getter
@Setter
@Table(name="t_e_collection_withdrawal_amount_change_record")
public class CollectionWithdrawalAmountChangeRecordEntity implements Serializable {
	
	private static final long serialVersionUID = -1L;
	
	/**
	 * 主键id
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	/**
	 * 对公户id
	 */
	private Long corporateAccountId;
	
	/**
	 * 银行唯一编号
	 */
	private String bankCollectionRecordNo;
	
	/**
	 * 到账金额(分)
	 */
	private Long arriveAmount;
	
	/**
	 * 变动前额度(分)
	 */
	private Long beforeAmount;
	
	/**
	 * 变动额度(分)
	 */
	private Long changeAmount;
	
	/**
	 * 变动后额度(分)
	 */
	private Long afterAmount;
	
	/**
	 * 核销时间
	 */
	private Date changeTime;
	
	/**
	 * 核销订单号
	 */
	private String orderNo;
	
	/**
	 * 核销类型 1-提现核销 2-手动核销 3-金额退还
	 */
	private Integer changeType;
	
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
	 * 开票订单编号
	 */
	private String invoiceOrderNo;
}
