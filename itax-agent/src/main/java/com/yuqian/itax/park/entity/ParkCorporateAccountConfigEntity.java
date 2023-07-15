package com.yuqian.itax.park.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;
/**
 * 园区对公户提现配置
 * 
 * @Date: 2020年09月08日 10:53:15 
 * @author 蒋匿
 */
@Getter
@Setter
@Table(name="t_e_park_corporate_account_config")
public class ParkCorporateAccountConfigEntity implements Serializable {
	
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
	 * 对公户银行名称
	 */
	private String corporateAccountBankName;
	
	/**
	 * 渠道账户
	 */
	private String channelAccount;
	
	/**
	 * 密钥
	 */
	private String secKey;
	
	/**
	 * url地址
	 */
	private String url;
	
	/**
	 * 参数值
	 */
	private String paramsValues;
	
	/**
	 * 公钥
	 */
	private String publicKey;
	
	/**
	 * 私钥
	 */
	private String privateKey;
	
	/**
	 * 状态 0-不可用 1-可用
	 */
	private Integer status;
	
	/**
	 * 银行开户费(仅展示)
	 */
	private String bankActivationFee;
	
	/**
	 * 银行其他费用(仅展示)
	 */
	private String bankOtherFee;
	
	/**
	 * 银行提现手续费(仅展示)
	 */
	private String bankWithdrawalFee;

	/**
	 * 单笔提现限额（元）
	 */
	private Long singleWithdrawalLimit;

	/**
	 * 单日提现限额（元）
	 */
	private Long dailyWithdrawalLimit;
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
	 * 银行总部名称
	 */
	private String headquartersName;

	/**
	 * 银行总部编号
	 */
	private String headquartersNo;
}
