package com.yuqian.itax.system.entity.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @Description 行业列表VO
 * @Author  Kaven
 * @Date   2020/8/14 17:25
*/
@Getter
@Setter
public class IndustryVO implements Serializable {
	
	private static final long serialVersionUID = -1L;
	/**
	 * 行业id
	 */
	@ApiModelProperty(value = "行业id")
	private Long id;

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