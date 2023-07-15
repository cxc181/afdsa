package com.yuqian.itax.user.entity.vo;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.jeecgframework.poi.excel.annotation.Excel;

import java.io.Serializable;
import java.util.Date;

/**
 * @Description 企业对公户账户明细VO
 * @Author  Kaven
 * @Date   2020/9/7 15:54
*/
@Getter
@Setter
public class CorporateAccountCollectionRecordVO implements Serializable {
	
	private static final long serialVersionUID = -1L;

	/**
	 * 主键ID
	 */
	private Long id;

	/**
	 * 银行唯一编号
	 */
	@Excel(name="银行唯一编号")
	private String bankCollectionRecordNo;

	/**
	 * 交易时间
	 */
	@Excel(name="交易时间")
	private String tradingTime;

	/**
	 * 对方账户名
	 */
	@Excel(name="对方账户名")
	private String otherPartyBankAccount;

	/**
	 * 借方发生额(分)
	 */
	@Excel(name="借方发生额(分)")
	private Long dhamt;
	/**
	 * 贷方发生额(分)
	 */
	@Excel(name="贷方发生额(分)")
	private Long crHpnam;
	/**
	 * 发生金额（收款金额）
	 */
	@Excel(name="发生金额")
	private Long hpnAmt;

	/**
	 * 账户余额(分)
	 */
	@Excel(name="账户余额")
	private Long acba;

	/**
	 * 对方开户行
	 */
	@Excel(name="对方开户行")
	private String otherPartyBankName;
	/**
	 * 对方账户
	 */
	@Excel(name="对方账户")
	private String otherPartyBankNumber;
	/**
	 * 剩余提现额度(分)
	 */
	@Excel(name="剩余提现额度")
	private Long remainingWithdrawalAmount;

	/**
	 * 备注
	 */
	@Excel(name="备注")
	private String remark;
	/**
	 * 交易备注
	 */
	@Excel(name="交易备注")
	private String tradingRemark;

	/**
	 * 摘要
	 */
	@Excel(name="摘要")
	private String smy;

	/**
	 * 收支类型
	 */
	private Integer tradingStatus;


	/**
	 * 对方账户加敏
	 * @param otherPartyBankNumber
	 */
	public void setOtherPartyBankNumber(String otherPartyBankNumber) {
		if (StringUtils.isBlank(otherPartyBankNumber)) {
			return;
		}
		this.otherPartyBankNumber = StringUtils.overlay(otherPartyBankNumber, "****", 4, otherPartyBankNumber.length() - 4);
	}
}
