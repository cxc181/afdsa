package com.yuqian.itax.system.entity.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 行业接收dto
 * @Date: 2020年07月20日
 * @author yejian
 */
@Getter
@Setter
public class IndustryApiDTO implements Serializable {
	
	private static final long serialVersionUID = -1L;
	private String oemCode;

	/**
	 * 园区Code
	 */
	@NotBlank(message="园区Code不能为空")
	@ApiModelProperty(value = "园区Code", required = true)
	private String parkCode;
	
	/**
	 * 企业类型
	 */
	@NotNull(message="企业类型不能为空")
	@Min(value = 1, message = "企业类型有误")
	@Max(value = 4, message = "企业类型有误")
	@ApiModelProperty(value = "企业类型：1-个体开户 2-个独开户 3-有限合伙 4-有限责任")
	private Integer companyType = 1;

}