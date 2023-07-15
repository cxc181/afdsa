package com.yuqian.itax.order.entity.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 开票补传流水订单返回VO
 * @Date: 2020年07月20日 15:05
 * @author yejian
 */
@Getter
@Setter
public class InvoiceWaterOrderApiVO implements Serializable {
	
	private static final long serialVersionUID = -1L;
	
	/**
	 * 订单号
	 */
	@ApiModelProperty(value = "订单号")
	private String orderNo;

	/**
	 * 开票公司名称
	 */
	@ApiModelProperty(value = "开票公司名称")
	private String companyName;

	/**
	 * 流水状态 0-待上传 1-审核中 2-审核通过 3-审核不通过 4-流水前置
	 */
	@ApiModelProperty(value = "流水状态 0-待上传 1-审核中 2-审核通过 3-审核不通过 4-流水前置")
	private Integer bankWaterStatus;

	/**
	 * 审核失败原因
	 */
	@ApiModelProperty(value = "审核失败原因")
	private String auditErrorRemark;

}
