package com.yuqian.itax.snapshot.entity.vo;

import lombok.Getter;
import lombok.Setter;
import org.jeecgframework.poi.excel.annotation.Excel;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 开票订单快照(已完成)
 * 
 * @Date: 2020年10月26日 11:25:23 
 * @author 蒋匿
 */
@Getter
@Setter
public class InvoiceSnapshotVO implements Serializable {
	
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
	 * 新增开票金额
	 */
	@Excel(name = "新增开票金额", width = 20)
	private BigDecimal addTotalAmt = BigDecimal.ZERO.setScale(2);

	/**
	 * 新增专票金额
	 */
	@Excel(name = "新增专票金额", width = 20)
	private BigDecimal addZpAmt = BigDecimal.ZERO.setScale(2);

	/**
	 * 新增普票金额
	 */
	@Excel(name = "新增普票金额", width = 20)
	private BigDecimal addPpAmt = BigDecimal.ZERO.setScale(2);

	/**
	 * 开票用户数
	 */
	@Excel(name = "开票用户数", width = 20)
	private Integer userNum = 0;

	/**
	 * 开票个体数
	 */
	@Excel(name = "开票个体数", width = 20)
	private Integer companyNum = 0;

	/**
	 * 完成订单数
	 */
	@Excel(name = "完成订单数", width = 20)
	private Integer orderNum = 0;

	/**
	 * 累计开票总金额
	 */
	@Excel(name = "累计开票总金额", width = 20)
	private BigDecimal totalAmt = BigDecimal.ZERO.setScale(2);

	/**
	 * 累计专票总金额
	 */
	@Excel(name = "累计专票总金额", width = 20)
	private BigDecimal zpAmt = BigDecimal.ZERO.setScale(2);

	/**
	 * 累计普票总金额
	 */
	@Excel(name = "累计普票总金额", width = 20)
	private BigDecimal ppAmt = BigDecimal.ZERO.setScale(2);

	
}
