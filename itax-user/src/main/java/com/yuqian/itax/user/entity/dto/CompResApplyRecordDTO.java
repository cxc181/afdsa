package com.yuqian.itax.user.entity.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.*;
import java.io.Serializable;

/**
 * 企业资源申请记录前端参数接收bean
 * @Date: 2020年03月25日 11:03:46
 * @author yejian
 */
@Getter
@Setter
public class CompResApplyRecordDTO implements Serializable {
	
	private static final long serialVersionUID = -1L;

	/**
	 * 企业id
	 */
	@NotNull(message = "企业id不能为空")
	@ApiModelProperty(value = "企业id",required = true)
	private Long companyId;

	/**
	 * 申请类型 1-领用 2-归还
	 */
	@NotNull(message = "申请类型不能为空")
	@Min(value = 1, message = "申请类型有误")
	@Max(value = 2, message = "申请类型有误")
	@ApiModelProperty(value = "申请类型 1-领用 2-归还",required = true)
	private Integer applyType;
	
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
    @NotBlank(message = "收件人详细地址不能为空")
    @ApiModelProperty(value = "收件人详细地址", required = true)
    private String recipientAddress;

    /**
     * 快递单号
     */
    @ApiModelProperty(value = "快递单号")
    private String courierNumber;

    /**
     * 快递公司名称
     */
    @ApiModelProperty(value = "快递公司名称")
    private String courierCompanyName;
}
