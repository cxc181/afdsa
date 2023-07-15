package com.yuqian.itax.order.entity.vo;

import com.yuqian.itax.order.entity.InvoiceServiceFeeDetailEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 开票订单支付金额VO
 * 
 * @Date: 2019年12月13日 15:05:12
 * @author yejian
 */
@Getter
@Setter
public class PayInformationVO implements Serializable {

	private static final long serialVersionUID = -1L;

	/**
	 * 开票金额
	 */
	@ApiModelProperty(value = "开票金额")
	private Long invoiceAmount;

	/**
	 * 支付金额
	 */
	@ApiModelProperty(value = "支付金额")
	private Long payAmount;

	/**
	 * 合计税费（本次应缴增值税+本次应缴所得税+本次应缴附加税）
	 */
	@ApiModelProperty(value = "合计税费（本次应缴增值税+本次应缴所得税+本次应缴附加税）")
	private Long allTax;

	/**
	 * 服务费
	 */
	@ApiModelProperty(value = "服务费")
	private Long serviceFee;

	/**
	 * 服务费优惠
	 */
	@ApiModelProperty(value = "服务费优惠")
	private Long serviceFeeDiscount;

	/**
	 * 邮寄费金额
	 */
	@ApiModelProperty(value = "邮寄费金额")
	private Long postageFees;

	/**
	 * 本周期（托管周期）历史开票金额
	 */
	private Long historicalInvoiceAmount;

	/**
	 * 税费明细
	 */
	@ApiModelProperty(value = "税费明细")
	private TaxFeeDetailVO taxFeeDetail;

	/**
	 * 服务费明细
	 */
	@ApiModelProperty(value = "服务费明细")
	private List<InvoiceServiceFeeDetailVO> invoiceServiceFeeDetail;
}
