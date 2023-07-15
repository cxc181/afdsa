package com.yuqian.itax.snapshot.entity.vo;

import lombok.Getter;
import lombok.Setter;
import org.jeecgframework.poi.excel.annotation.Excel;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Date;

/**
 * 订单快照(已完成)
 * 
 * @Date: 2020年10月26日 10:48:25 
 * @author 蒋匿
 */
@Getter
@Setter
public class RechargeWithdrawSnapshotVO implements Serializable {
	
	private static final long serialVersionUID = -1L;

	/**
	 * 机构编码
	 */
	private String oemCode;

	/**
	 * 机构名称
	 */
	@Excel(name = "OEM机构", width = 30)
	private String oemName;

	/**
	 * 充值金额
	 */
	@Excel(name = "充值金额", type = 10, width = 30)
	private BigDecimal rechargeAmt = BigDecimal.ZERO.setScale(2);

	/**
	 * 提现金额
	 */
	@Excel(name = "提现金额", type = 10, width = 30)
	private BigDecimal withdrawAmt = BigDecimal.ZERO.setScale(2);

}
