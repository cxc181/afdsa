package com.yuqian.itax.user.entity.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * 发票抬头
 * 
 * @Date: 2019年12月07日 20:48:40 
 * @author yejian
 */
@Getter
@Setter
public class EditInvoiceHeadDTO implements Serializable {
	
	private static final long serialVersionUID = -1L;

	/**
	 * 主键id
	 */
	@NotNull(message="主键id不能为空")
	@ApiModelProperty(value = "主键id",required = true)
	private Long id;

	/**
	 * 公司名称
	 */
	@NotBlank(message="公司名称不能为空")
	@ApiModelProperty(value = "公司名称",required = true)
	@Size(max = 64, message = "公司名称不能超过64位字符")
	private String companyName;

	/**
	 * 公司地址
	 */
	@ApiModelProperty(value = "公司地址",required = true)
	@Size(max = 43, message = "公司地址不能超过43位字符")
	private String companyAddress;

	/**
	 * 税号
	 */
	@NotBlank(message="税号不能为空")
	@Pattern(regexp = "^[A-Z0-9]{15}$|^[A-Z0-9]{18}$|^[A-Z0-9]{20}$", message = "税号格式不正确")
	@ApiModelProperty(value = "税号",required = true)
	private String ein;
	
	/**
	 * 电话号码
	 */
	@ApiModelProperty(value = "电话号码",required = true)
	private String phone;
	
	/**
	 * 开户银行
	 */
	@ApiModelProperty(value = "开户银行",required = true)
	@Size(max = 64, message = "开户银行不能超过64位字符")
	private String bankName;
	
	/**
	 * 银行账号
	 */
	@ApiModelProperty(value = "银行账号",required = true)
	@Size(max = 32, message = "银行账号不能超过32位字符")
	private String bankNumber;
	
	/**
	 * 备注
	 */
	@ApiModelProperty(value = "备注")
	@Size(max = 128, message = "详细地址不能超过128位字符")
	private String remark;

}
