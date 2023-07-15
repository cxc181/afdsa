package com.yuqian.itax.user.entity.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.io.Serializable;

/**
 * 企业证件归还接收dto
 * @Date: 2020年07月21日 10:03:46
 * @author yejian
 */
@Getter
@Setter
public class CompanyCertReturnApiDTO implements Serializable {
	
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
	 * 快递单号
	 */
	@NotBlank(message="快递单号不能为空")
	@Pattern(regexp = "^[A-Za-z0-9]{4,40}$", message = "请输入4~40位，支持字母/数字组合")
	@ApiModelProperty(value = "快递单号",required = true)
	private String courierNumber;

	/**
	 * 快递公司名称
	 */
	@NotBlank(message="快递公司名称不能为空")
	@ApiModelProperty(value = "快递公司名称",required = true)
	private String courierCompanyName;
}
