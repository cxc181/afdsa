package com.yuqian.itax.snapshot.entity.vo;

import lombok.Getter;
import lombok.Setter;
import org.jeecgframework.poi.excel.annotation.Excel;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * oem机构资金流统计
 * 
 * @Date: 2020年10月26日 11:25:23 
 * @author 蒋匿
 */
@Getter
@Setter
public class OemCapitalVO implements Serializable {
	
	private static final long serialVersionUID = -1L;

	/**
	 * 机构编码
	 */
	private String oemCode;

	/**
	 * 机构名称
	 */
	@Excel(name = "OEM机构", width = 20)
	private String oemName;

	/**
	 * 新增托管费
	 */
	@Excel(name = "新增托管费", width = 20)
	private BigDecimal registerAmt = BigDecimal.ZERO.setScale(2);

	/**
	 * 新增开票服务费
	 */
	@Excel(name = "新增开票服务费", width = 20)
	private BigDecimal serviceFee = BigDecimal.ZERO.setScale(2);

	/**
	 * 新增快递费
	 */
	@Excel(name = "新增快递费", width = 20)
	private BigDecimal postageFees = BigDecimal.ZERO.setScale(2);

	/**
	 * 新增税费
	 */
	@Excel(name = "新增税费", width = 20)
	private BigDecimal totalTaxFee = BigDecimal.ZERO.setScale(2);

	/**
	 * 新增会员升级费
	 */
	@Excel(name = "新增会员升级费", width = 20)
	private BigDecimal upgradeAmt = BigDecimal.ZERO.setScale(2);

	/**
	 * 新增公户申请费
	 */
	@Excel(name = "新增公户申请费", width = 20)
	private BigDecimal corporateAmt = BigDecimal.ZERO.setScale(2);

	/**
	 * 新增托管费续费
	 */
	@Excel(name = "新增托管费续费", width = 20)
	private BigDecimal custodyAmt = BigDecimal.ZERO.setScale(2);

	/**
	 * 新增利润
	 */
	@Excel(name = "新增利润", width = 20)
	private BigDecimal addProfitAmt = BigDecimal.ZERO.setScale(2);

	/**
	 * 总利润
	 */
	@Excel(name = "总利润", width = 20)
	private BigDecimal totalProfitAmt = BigDecimal.ZERO.setScale(2);

	
}
