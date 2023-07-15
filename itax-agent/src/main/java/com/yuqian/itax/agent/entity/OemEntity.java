package com.yuqian.itax.agent.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 机构管理
 * 
 * @Date: 2019年12月07日 20:32:18 
 * @author 蒋匿
 */
@Getter
@Setter
@Table(name="t_e_oem")
public class OemEntity implements Serializable {
	
	private static final long serialVersionUID = -1L;
	
	/**
	 * 主键id
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	/**
	 * 机构名称
	 */
	private String oemName;
	
	/**
	 * 机构编码
	 */
	private String oemCode;
	
	/**
	 * 机构联系人
	 */
	private String oemUser;
	
	/**
	 * 联系人电话
	 */
	private String oemPhone;
	
	/**
	 * 机构状态 机构状态 0-下架1-上架 2-暂停 3-待上架
	 */
	private Integer oemStatus;
	
	/**
	 * 机构logo
	 */
	private String oemLogo;
	
	/**
	 * 公司名称
	 */
	private String companyName;
	
	/**
	 * 运营网址
	 */
	private String netAddress;

	/**
	 * 结算周期
	 */
	private Integer settlementCycle;

	/**
	 * 结算类型 1-按周 2-按月
	 */
	private Integer settlementType;

	/**
	 * 客服电话
	 */
	private String customerServiceTel;

	/**
	 * 城市服务商以下提现手续费率
	 */
	private BigDecimal commissionServiceFeeRate;

	/**
	 * 城市服务商提现手续费率
	 */
	private BigDecimal diamondCommissionServiceFeeRate;

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
	 * 秘钥
	 */
	private String oemSecret;
	/**
	 * 公司地址
	 */
	private String belongsCompanyAddress;

	/**
	 * 统一社会信用代码
	 */
	private String ein;

	/**
	 * 是否邀请人校验  0-不校验 1-校验
	 */
	private Integer isInviterCheck;

	/**
	 * 是否开通推广中心 0-否 1-是
	 */
	private Integer isOpenPromotion;

	/**
	 * 邀请员工上限
	 */
	private Integer employeesLimit;

	/**
	 * 小程序版本号
	 */
	private String versionCode;

	/**
	 * 使用方式  1-小程序 2-h5  3-半api  4-全api  ,支持多种使用方式并存，方式之间用 逗号 分割
	 */
	private String useWay;

	/**
	 * 消费钱包最小限额
	 */
	private Long minConsumptionWalletLimit;

	/**
	 * 佣金钱包最小限额
	 */
	private Long minCommissionWalletLimit;

	/**
	 * 邀请人账号
	 */
	private String inviterAccount;

	/**
	 * 默认短信验证码
	 */
	private String defaultSmsCode;
	/**
	 * 是否大客户 0-否 1-是
	 */
	private Integer isBigCustomer;

	/**
	 * 工单审核方 1-平台客服  2-oem机构客服
	 */
	private Integer workAuditWay;

	/**
	 * 协议模板id
	 */
	private Long agreementTemplateId;

	/**
	 * 公章图片地址
	 */
	private String officialSealImg;

	/**
	 * 公章图片地址（公域）
	 */
	private String officialSealImgPublic;

	/**
	 * 是否发送查账征收税单消息   0-不发送  1-发送
	 */
	private Integer isSendAuditBillsMessage;

	/**
	 * oem小程序appid
	 */
	private String oemAppid;

	/**
	 * 收单机构oemcode
	 */
	private String otherPayOemcode;

	/**
	 * 是否其他oem机构收单  0-否 1-是
	 */
	private Integer isOtherOemPay;

	/**
	 * 是否收银台 0-否 1-是
	 */
	private Integer isCheckstand;

	/**
	 * 收款账号开户行
	 */
	private String receivingBankAccountBranch;

	/**
	 * 收款银行账号
	 */
	private String receivingBankAccount;

	/**
	 * 消费钱包提现说明
	 */
	private String consumptionWithdrawExplain;

	/**
	 * 消费钱包提现免费额度（分）
	 */
	private Long consumptionWithdrawFreeCredit;

	/**
	 * 消费钱包提现手续费率
	 */
	private BigDecimal consumptionWithdrawRate;

	/**
	 * 佣金钱包提现单笔最高限额（分）
	 */
	private Integer maxCommissionWithdrawSingleLimit;

	/**
	 * 佣金钱包提现单月限额（分）
	 */
	private Integer commissionWithdrawMonthLimit;

	/**
	 * 消费钱包提现充值天数限制
	 */
	private Integer rechargeDays;
}
