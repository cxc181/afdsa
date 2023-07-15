package com.yuqian.itax.user.entity.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

/**
 * 企业列表-接入方
 *
 * @author lmh
 * @Date: 2021/8/12
 */
@Getter
@Setter
public class CompanyListOfAccessPartyVO implements Serializable {

	private static final long serialVersionUID = -1L;

	/**
	 * 企业id
	 */
	@ApiModelProperty(value = "企业id")
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
	 * 状态 1-正常 2-禁用 4-已注销 5-注销中
	 */
	@ApiModelProperty(value = "状态：1->正常；2->禁用 4->已注销 5->注销中")
	private Integer status;

	/**
	 * 园区名称
	 */
	@ApiModelProperty(value = "园区名称")
	private String parkName;

	/**
	 * 出证时间
	 */
	@ApiModelProperty("出证时间")
	private Date addTime;

	/**
	 * 税号
	 */
	@ApiModelProperty("税号")
	private String ein;

	/**
	 * 行业
	 */
	@ApiModelProperty("行业")
	private String industry;

	/**
	 * 经营范围
	 */
	@ApiModelProperty("经营范围")
	private String businessScope;

	/**
	 * 近12个月可开票金额
	 */
	@ApiModelProperty("近12个月可开票金额")
	private Long invoicingAmount;

	/**
	 * 营业执照副本
	 */
	@ApiModelProperty("营业执照")
	private String businessLicenseCopy;
}
