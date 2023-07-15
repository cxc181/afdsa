package com.yuqian.itax.user.entity.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 企业证件申请接收dto
 * @Date: 2020年07月21日 10:03:46
 * @author yejian
 */
@Getter
@Setter
public class CompanyCertUseApiDTO implements Serializable {
	
	private static final long serialVersionUID = -1L;

	/**
	 * 外部订单号
	 */
	@NotBlank(message="外部订单号不能为空")
	@ApiModelProperty(value = "外部订单号",required = true)
	private String externalOrderNo;

	/**
	 * 企业id
	 */
	@NotNull(message = "企业id不能为空")
	@ApiModelProperty(value = "企业id",required = true)
	private Long companyId;

	/**
	 * 会员账号
	 */
	@NotBlank(message="会员账号不能为空")
	@ApiModelProperty(value = "会员账号",required = true)
	private String regPhone;

	/**
	 * 资源类型 1-公章 2-财务章 3-对公账号u盾 4-营业执照 5-营业执照副本 6-发票章；多个资源直接用逗号分割
	 */
	@NotBlank(message="资源类型不能为空")
	@ApiModelProperty(value = "资源类型 1-公章 2-财务章 3-对公账号u盾 4-营业执照 5-营业执照副本 6-发票章；多个资源直接用逗号分割",required = true)
	private String applyResouces;

	/**
	 * 收件人姓名
	 */
	@NotBlank(message="收件人姓名不能为空")
	@ApiModelProperty(value = "收件人姓名",required = true)
	private String recipient;
	
	/**
	 * 收件人手机号
	 */
	@NotBlank(message="收件人手机号不能为空")
	@ApiModelProperty(value = "收件人手机号",required = true)
	private String recipientPhone;
	
	/**
	 * 收件人省编码
	 */
	@NotBlank(message="收件人省编码不能为空")
	@ApiModelProperty(value = "收件人省编码",required = true)
	private String provinceCode;
	
	/**
	 * 收件人省名称
	 */
	@NotBlank(message="收件人省名称不能为空")
	@ApiModelProperty(value = "收件人省名称",required = true)
	private String provinceName;
	
	/**
	 * 收件人市编码
	 */
	@NotBlank(message="收件人市编码不能为空")
	@ApiModelProperty(value = "收件人市编码",required = true)
	private String cityCode;
	
	/**
	 * 收件人市名称
	 */
	@NotBlank(message="收件人市名称不能为空")
	@ApiModelProperty(value = "收件人市名称",required = true)
	private String cityName;
	
	/**
	 * 收件人区编码
	 */
	@NotBlank(message="收件人区编码不能为空")
	@ApiModelProperty(value = "收件人区编码",required = true)
	private String districtCode;
	
	/**
	 * 收件人区名称
	 */
	@NotBlank(message="收件人区名称不能为空")
	@ApiModelProperty(value = "收件人区名称",required = true)
	private String districtName;
	
	/**
	 * 收件人详细地址
	 */
	@NotBlank(message="收件人详细地址不能为空")
	@ApiModelProperty(value = "收件人详细地址",required = true)
	private String recipientAddress;

}
