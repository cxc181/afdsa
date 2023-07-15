package com.yuqian.itax.corporateaccount.vo;

import cn.hutool.core.lang.copier.SrcToDestCopier;
import com.yuqian.itax.common.base.entity.query.BaseQuery;
import com.yuqian.itax.util.util.StringUtil;
import lombok.Getter;
import lombok.Setter;
import org.jeecgframework.poi.excel.annotation.Excel;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 *  对公户提现订单表
 * 
 * @Date: 2020年09月07日 09:12:30 
 * @author 蒋匿
 */
@Getter
@Setter
public class CorporateAccountWithdrawWaterVO extends BaseQuery implements Serializable {
	
	private static final long serialVersionUID = -1L;
	/**
	 * 流水号
	 */
	@Excel(name = "流水号", width = 26)
	private String payNo;

	/**
	 * 流水类型 1-充值，2-提现 ，3-余额支付，4-第三方支付，5-退款 6-对公户提现
	 */
	@Excel(name = "流水类型" , replace = { "-_null","充值_1","提现_2","余额支付_3","第三方支付_4","退款_5","对公户提现_6" }, width = 12)
	private Integer payWaterType;

	/**
	 * 外部流水号
	 */
	@Excel(name = "外部流水号", width = 24)
	private String externalOrderNo;

	/**
	 * 订单号
	 */
	@Excel(name = "订单编号", width = 24)
	private String orderNo;

	/**
	 * 订单类型 1-充值 2-代理充值 3-提现 4-代理提现 5 - 工商注册 6-开票 7-用户升级 8-工商注销 9-证件申请 10-对公户申请 11-对公户提现
	 */
	@Excel(name = "订单类型" , replace = { "-_null","充值_1","代理充值_2","提现_3","代理提现_4","工商开户_5","开票_6","用户升级_7","工商注销_8","证件申请_9","公户申请_10","公户提现_11" })
	private Integer orderType;

	/**
	 * 企业名称(出款账户名)
	 */
	@Excel(name = "出款账户名", width = 20)
	private String companyName;

	/**
	 * 对公账户(出款账户)
	 */
	@Excel(name = "出款账户", width = 20)
	private String corporateAccount;

	/**
	 * 对公户银行名称（开户行）
	 */
	@Excel(name = "开户行", width = 20)
	private String corporateAccountBankName;

	/**
	 * 出款金额(元)
	 */
	@Excel(name = "出款金额")
	private BigDecimal withdrawalAmount;

	/**
	 * 创建时间
	 */
	@Excel(name = "创建时间", replace = {
			"-_null" }, databaseFormat = "yyyyMMddHHmmss", format = "yyyy-MM-dd HH:mm:ss", width = 20)
	private Date addTime;

	/**
	 * 支付方式  1-微信 2-余额 3-支付宝 4-快捷支付
	 */
	@Excel(name = "支付方式", replace = { "-_null","微信_1","余额_2","支付宝_3","快捷支付_4" })
	private Integer payWay;

	/**
	 * 支付通道 1-微信 2-余额 3-支付宝 4-易宝支付 5-建行 6-北京代付 7-纳呗
	 */
	@Excel(name = "支付通道", replace = { "-_null","微信_1","余额_2","支付宝_3","易宝支付_4","建行_5","北京代付_6","纳呗_7" })
	private Integer payChannels;

	/**
	 * 支付状态 0-待支付 1-支付中 2 -支付成功 3-支付失败
	 */
	@Excel(name = "支付状态", replace = { "-_null","待支付_0","支付中_1","支付成功_2","支付失败_3" })
	private Integer payStatus;

//	/**
//	 * 到账人姓名
//	 */
//	@Excel(name = "到账人姓名")
//	private String arriveUserName;

	/**
	 * 到账银行账户（到账卡号）
	 */
	@Excel(name = "到账卡号", width = 20)
	private String arriveBankAccount;
	
	/**
	 * 到账银行名称（到账银行）
	 */
	@Excel(name = "到账银行", width = 20)
	private String arriveBankName;

	/**
	 * 所属OEM
	 */
	@Excel(name = "所属OEM", width = 15)
	private String oemName;

	/**
	 * 园区名称
	 */
	@Excel(name = "园区名称")
	private String parkName;
}
