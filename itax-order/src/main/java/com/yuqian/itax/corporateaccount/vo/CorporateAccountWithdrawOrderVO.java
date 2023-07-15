package com.yuqian.itax.corporateaccount.vo;

import com.yuqian.itax.common.base.entity.query.BaseQuery;
import com.yuqian.itax.util.util.StringUtil;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
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
public class CorporateAccountWithdrawOrderVO extends BaseQuery implements Serializable {
	
	private static final long serialVersionUID = -1L;
	
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
	 * 用户手机号（会员账号）
	 */
	@Excel(name = "会员账号", width = 16)
	private String memberPhone;

	/**
	 * 企业名称(账户名)
	 */
	@Excel(name = "账户名", width = 20)
	private String companyName;

	/**
	 * 对公账户(账户)
	 */
	@Excel(name = "账户", width = 20)
	private String corporateAccount;

	/**
	 * 对公户银行名称（开户行）
	 */
	@Excel(name = "开户行", width = 20)
	private String corporateAccountBankName;

	/**
	 * 提现金额(元)
	 */
	@Excel(name = "提现金额")
	private BigDecimal withdrawalAmount;

	/**
	 * 会员等级 -1-员工 0-普通会员 1-VIP 2-白银会员 3-税务顾问 4-铂金会员 5-城市服务商
	 */
	@Excel(name = "会员等级", replace = { "-_null","员工_-1","普通会员_0","VIP_1","白银会员_2","税务顾问_3","铂金会员_4","城市服务商_5" })
	private Integer memberLevel;

	/**
	 * 添加时间
	 */
	@Excel(name = "创建时间", replace = {
			"-_null" }, databaseFormat = "yyyyMMddHHmmss", format = "yyyy-MM-dd HH:mm:ss", width = 20)
	private Date addTime;

	/**
	 * 到账人姓名
	 */
	@Excel(name = "到账人姓名", width = 15)
	private String arriveUserName;

	/**
	 * 到账银行账户（到账银行卡号）
	 */
	@Excel(name = "到账银行卡号", width = 20)
	private String arriveBankAccount;
	
	/**
	 * 到账银行名称（银行名称）
	 */
	@Excel(name = "银行名称", width = 20)
	private String arriveBankName;

	/**
	 * 订单状态 (0-待提交,1-支付、提现中,2-支付、提现完成,3-支付、提现失败,4-订单过期,5-已取消,6-待财务审核,7-财务审核失败)
	 */
	@Excel(name = "订单状态", replace = { "-_null","待提交_0","支付中_1","支付成功_2","支付失败_3","订单过期_4","已取消_5","待财务审核_6","财务审核失败_7" })
	private Integer orderStatus;

	/**
	 * 所属OEM
	 */
	@Excel(name = "所属OEM", width = 15)
	private String oemName;

	/**
	 * 对公户提现备注
	 */
	private String remark;

	/**
	 * 园区名称
	 */
	@Excel(name = "园区名称")
	private String parkName;

	public void setCorporateAccount(String corporateAccount) {
		if (StringUtils.isBlank(corporateAccount)) {
			return;
		}
		this.corporateAccount = StringUtils.overlay(corporateAccount, "****", 4, corporateAccount.length() - 4);
	}

	public void setArriveBankAccount(String arriveBankAccount) {
		if (StringUtils.isBlank(arriveBankAccount)) {
			return;
		}
		this.arriveBankAccount = StringUtils.overlay(arriveBankAccount, "****", 4, arriveBankAccount.length() - 4);
	}
}
