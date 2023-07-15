package com.yuqian.itax.system.entity.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 行业返回VO
 * @Date: 2020年07月20日
 * @author yejian
 */
@Getter
@Setter
public class IndustryApiVO implements Serializable {
	
	private static final long serialVersionUID = -1L;

	/**
	 * 行业id
	 */
	@ApiModelProperty(value = "行业id")
	private Long industryId;
	
	/**
	 * 行业名称
	 */
	@ApiModelProperty(value = "行业名称")
	private String industryName;

	/**
	 * 示例名称
	 */
	@ApiModelProperty(value = "示例名称")
	private String exampleName;

	/**
	 * 行业备注
	 */
	@ApiModelProperty(value = "行业备注")
	private String remark;

}