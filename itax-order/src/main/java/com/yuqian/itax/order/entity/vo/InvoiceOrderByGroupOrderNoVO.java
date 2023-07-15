package com.yuqian.itax.order.entity.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.jeecgframework.poi.excel.annotation.Excel;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 开票订单
 * 
 * @Date: 2019年12月07日 20:05:12 
 * @author 蒋匿
 */
@Getter
@Setter
public class InvoiceOrderByGroupOrderNoVO implements Serializable {
	
	private static final long serialVersionUID = -1L;

	/**
	 * 集团开票订单号
	 */
	@Excel(name = "批量开票订单编号", width = 25)
	private String groupOrderNo;

	/**
	 * 订单号
	 */
	@Excel(name = "子订单编号", width = 25)
	private String orderNo;

	/**
	 * 会员手机号
	 */
	@Excel(name = "注册账号", width = 18)
	private String memberPhone;

	/**
	 * 企业名称
	 */
	@Excel(name = "开票企业", width = 25)
	private String companyName;

	/**
	 * 园区名称
	 */
	@Excel(name = "园区")
	private String parkName;

	/**
	 * 经办人账号
	 */
	@Excel(name = "经办人账号")
	private String agentAccount;

	/**
	 * 发票方式 1-纸质发票 2-电子发票
	 */
	@Excel(name = "发票方式 " , replace = { "纸质发票_1","电子发票_2" })
	private Integer invoiceWay;
	/**
	 * 开票金额
	 */
	@Excel(name = "开票金额（元）")
	private BigDecimal invoiceAmount;
	
	/**
	 * 增值税费
	 */
	@Excel(name = "增值税（元）")
	private BigDecimal vatFee;

	/**
	 * 附加税
	 */
	@Excel(name = "附加税（元）")
	private BigDecimal surcharge;

	/**
	 * 所得税
	 */
	@Excel(name = "所得税（元）")
	private BigDecimal personalIncomeTax;

	/**
	 * 增值税补缴
	 */
//	@Excel(name = "增值税补缴")
	private BigDecimal vatPayment;

	/**
	 * 附加税补缴
	 */
//	@Excel(name = "附加税补缴")
	private BigDecimal surchargePayment;
	/**
	 * 所得税补缴
	 */
//	@Excel(name = "所得税补缴")
	private BigDecimal incomeTaxPayment;

	//企业id
	private Long companyId;
}
