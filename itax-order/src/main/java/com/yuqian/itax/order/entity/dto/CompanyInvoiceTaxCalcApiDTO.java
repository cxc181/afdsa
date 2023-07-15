package com.yuqian.itax.order.entity.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 企业开票税费计算接收dto
 * @Date: 2020年07月20日 09:25
 * @author yejian
 */
@Getter
@Setter
public class CompanyInvoiceTaxCalcApiDTO implements Serializable {
	
	private static final long serialVersionUID = -1L;

	/**
	 * 企业id
	 */
	@NotNull(message="企业id不能为空")
	@ApiModelProperty(value = "企业id", required = true)
	private Long companyId;

	/**
	 * 会员账号
	 */
	@NotBlank(message="会员账号不能为空")
	@ApiModelProperty(value = "会员账号", required = true)
	private String regPhone;

	/**
	 * 开票金额
	 */
	@NotNull(message="开票金额不能为空")
	@ApiModelProperty(value = "开票金额(分)", required = true)
	private Long invoiceAmount;

	/**
	 * 增值税税率
	 */
	@NotNull(message="增值税税率不能为空")
	@ApiModelProperty(value = "增值税税率", required = true)
	private BigDecimal vatFeeRate;

	/**
	 * 发票类型 1-增值税普通发票 2-增值税专用发票
	 */
	@NotNull(message="发票类型不能为空")
	@Min(value = 1, message = "发票类型有误")
	@Max(value = 2, message = "发票类型有误")
	@ApiModelProperty(value = "发票类型：1->增值税普通发票；2-增值税专用发票", required = true)
	private Integer invoiceType;

}