package com.yuqian.itax.corporateaccount.entity;

import java.io.Serializable;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;
/**
 *  对公户银行收款记录表
 * 
 * @Date: 2020年09月08日 11:31:18 
 * @author 蒋匿
 */
@Getter
@Setter
@Table(name="t_e_corporate_account_collection_record")
public class CorporateAccountCollectionRecordEntity implements Serializable {
	
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
	 * 摘要
	 */
	private String smy;
	
	/**
	 * 借方发生额(分)
	 */
	private Long dhamt;
	
	/**
	 * 贷方发生额(分)
	 */
	private Long crHpnam;
	
	/**
	 * 发生金额(分)
	 */
	private Long hpnAmt;
	
	/**
	 * 账户余额(分)
	 */
	private Long acba;
	
	/**
	 * 交易时间
	 */
	private Date tradingTime;
	
	/**
	 * 对方账户名
	 */
	private String otherPartyBankAccount;
	
	/**
	 * 对方开户行
	 */
	private String otherPartyBankName;
	
	/**
	 * 对方账户
	 */
	private String otherPartyBankNumber;
	
	/**
	 * 交易状态  1-支出 2-收入 
	 */
	private Integer tradingStatus;
	
	/**
	 * 交易备注
	 */
	private String tradingRemark;
	
	/**
	 * 剩余提现额度(分)
	 */
	private Long remainingWithdrawalAmount;
	
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
