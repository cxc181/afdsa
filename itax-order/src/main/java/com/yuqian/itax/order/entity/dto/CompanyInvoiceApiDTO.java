package com.yuqian.itax.order.entity.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 企业开票接收dto
 * @Date: 2020年07月17日 14:05:12
 * @author yejian
 */
@Getter
@Setter
public class CompanyInvoiceApiDTO implements Serializable {
	
	private static final long serialVersionUID = -1L;

	/**
	 * 外部订单号
	 */
	@NotBlank(message="外部订单号不能为空")
	@Size(max = 32, message = "外部订单号不能超过32位字符")
	@ApiModelProperty(value = "外部订单号", required = true)
	private String externalOrderNo;

	/**
	 * 企业id
	 */
	@NotNull(message="企业id不能为空")
	@ApiModelProperty(value = "企业id", required = true)
	private Long companyId;
	
	/**
	 * 开票金额
	 */
	@NotNull(message="开票金额不能为空")
	@ApiModelProperty(value = "开票金额", required = true)
	private Long invoiceAmount;

	/**
	 * 发票类型 1-增值税普通发票 2-增值税专用发票
	 */
	@NotNull(message="发票类型不能为空")
	@Min(value = 1, message = "发票类型有误")
	@Max(value = 2, message = "发票类型有误")
	@ApiModelProperty(value = "发票类型：1->增值税普通发票；2-增值税专用发票", required = true)
	private Integer invoiceType;

	/**
	 * 开票类目名称
	 */
	@NotBlank(message="开票类目名称不能为空")
	@ApiModelProperty(value = "开票类目名称", required = true)
	private String categoryName;

	/**
	 * 增值税税率
	 */
	@NotNull(message="增值税税率不能为空")
	@ApiModelProperty(value = "增值税税率", required = true)
	private BigDecimal vatFeeRate;

	/**
	 * 发票方式：1->纸质发票；2-电子发票
	 */
	@Min(value = 1, message = "发票方式有误")
	@Max(value = 2, message = "发票方式有误")
	@ApiModelProperty(value = "发票方式：1->纸质发票；2-电子发票")
	private Integer invoiceWay;

	/**
	 * 是否先开票后补流水 0-先开票后补流水 1-先上传流水再开票
	 */
	@NotNull(message="是否先开票后补流水不能为空")
	@ApiModelProperty(value = "是否先开票后补流水 0-先开票后补流水 1-先上传流水再开票", required = true)
	private Integer isAfterUploadBankWater;

	/**
	 * 银行流水截图（多张逗号拼接）
	 */
	@ApiModelProperty(value = "银行流水截图（多张逗号拼接），若先上传流水再开票则必填")
	private String accountStatement;

	/**
	 * 会员账号
	 */
	@NotBlank(message="会员账号不能为空")
	@ApiModelProperty(value = "会员账号", required = true)
	private String regPhone;

	/**
	 * 发票抬头公司
	 */
	@NotBlank(message="发票抬头公司不能为空")
	@ApiModelProperty(value = "发票抬头公司名称", required = true)
	private String companyName;

	/**
	 * 发票抬头税号
	 */
	@NotBlank(message="发票抬头税号不能为空")
	@Pattern(regexp = "^[A-Z0-9]{15}$|^[A-Z0-9]{18}$|^[A-Z0-9]{20}$", message = "税号格式不正确")
	@ApiModelProperty(value = "发票抬头税号", required = true)
	private String ein;

	/**
	 * 发票抬头公司地址
	 */
	@NotBlank(message="发票抬头公司地址不能为空")
	@ApiModelProperty(value = "发票抬头公司地址")
	private String companyAddress;

	/**
	 * 发票抬头电话号码
	 */
	@NotBlank(message="发票抬头电话号码不能为空")
	@ApiModelProperty(value = "发票抬头电话号码", required = true)
	private String companyPhone;

	/**
	 * 发票抬头开户银行
	 */
	@Size(max = 64, message = "发票抬头开户银行不能超过64位字符")
	@ApiModelProperty(value = "发票抬头开户银行")
	private String bankName;

	/**
	 * 发票抬头银行账号
	 */
	@Size(max = 32, message = "发票抬头银行账号不能超过32位字符")
	@ApiModelProperty(value = "发票抬头银行账号")
	private String bankNumber;

	/**
	 * 收件人姓名
	 */
	@NotBlank(message="收件人姓名不能为空")
	@ApiModelProperty(value = "收件人姓名", required = true)
	private String recipient;

	/**
	 * 收件人联系电话
	 */
	@NotBlank(message="收件人联系电话不能为空")
	@ApiModelProperty(value = "收件人联系电话", required = true)
	private String recipientPhone;

	/**
	 * 收件人详细地址
	 */
	@NotBlank(message="收件人详细地址不能为空")
	@ApiModelProperty(value = "收件人详细地址", required = true)
	private String recipientAddress;

	/**
	 * 收件人省编码
	 */
	@NotBlank(message="收件人省编码不能为空")
	@ApiModelProperty(value = "收件人省编码", required = true)
	private String provinceCode;

	/**
	 * 收件人省名称
	 */
	private String provinceName;

	/**
	 * 收件人市编码
	 */
	@NotBlank(message="收件人市编码不能为空")
	@ApiModelProperty(value = "收件人市编码", required = true)
	private String cityCode;

	/**
	 * 收件人市名称
	 */
	private String cityName;

	/**
	 * 收件人区编码
	 */
	@NotBlank(message = "收件人区编码不能为空")
	@ApiModelProperty(value = "收件人区编码", required = true)
	private String districtCode;

	/**
	 * 收件人区名称
	 */
	@ApiModelProperty(value = "收件人区名称")
	private String districtName;

	/**
	 * 业务合同
	 */
	@ApiModelProperty(value = "业务合同")
	private String businessContractImgs;

}