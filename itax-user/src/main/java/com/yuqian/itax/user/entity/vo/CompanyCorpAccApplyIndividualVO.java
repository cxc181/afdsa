package com.yuqian.itax.user.entity.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @Description 对公户申请个体列表VO
 * @Author yejian
 * @Date 2020/09/09 10:03
 */
@Getter
@Setter
public class CompanyCorpAccApplyIndividualVO implements Serializable {

	private static final long serialVersionUID = -1L;

	/**
	 * 公司ID
	 */
	@ApiModelProperty(value = "公司ID")
	private Long companyId;

	/**
	 * 公司名称
	 */
	@ApiModelProperty(value = "公司名称")
	private String companyName;

	/**
	 * 经营者名称
	 */
	@ApiModelProperty(value = "经营者名称")
	private String operatorName;

	/**
	 * 园区id
	 */
	@ApiModelProperty(value = "园区id")
	private Long parkId;

	/**
	 * 园区名称
	 */
	@ApiModelProperty(value = "园区名称")
	private String parkName;

	/**
	 * 个体对公户状态描述
	 */
	@ApiModelProperty(value = "个体对公户状态描述")
	private String statusDesc;

	/**
	 * 企业托管费过期状态
	 * 过期状态 1-正常 2-即将过期 3-已过期
	 */
	@ApiModelProperty(value = "企业托管费过期状态")
	private Integer overdueStatus;
}
