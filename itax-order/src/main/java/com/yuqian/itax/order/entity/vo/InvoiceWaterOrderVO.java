package com.yuqian.itax.order.entity.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 开票补传流水订单
 * 
 * @Date: 2020年05月15日 11:05:12
 * @author yejian
 */
@Getter
@Setter
public class InvoiceWaterOrderVO implements Serializable {
	
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
	 * 开票金额
	 */
	@ApiModelProperty(value = "开票金额")
	private Long invoiceAmount;

	/**
	 * 修改时间
	 */
	@ApiModelProperty(value = "修改时间")
	private Date updateTime;

	/**
	 * 流水状态 0-待上传 1-审核中 2-审核通过 3-审核不通过 4-流水前置
	 */
	@ApiModelProperty(value = "流水状态 0-待上传 1-审核中 2-审核通过 3-审核不通过 4-流水前置")
	private Integer bankWaterStatus;

	/**
	 * '审核失败原因
	 */
	@ApiModelProperty(value = "审核失败原因")
	private String auditErrorRemark;

	/**
	 * 银行流水截图
	 */
	@ApiModelProperty(value = "银行流水截图")
	private String accountStatement;
	/**
	 * 银行流水截图list
	 */
	@ApiModelProperty(value = "银行流水截图")
	private String[] accountStatementList;
	/**
	 * 创建时间
	 */
	@ApiModelProperty(value = "创建时间")
	private Date addTime;

	/**
	 * 签收时间
	 */
	@ApiModelProperty(value = "签收时间")
	private Date completeTime;

	/**
	 * 是否超时，1：未超时，2：超时
	 */
	@ApiModelProperty(value = "是否超时，1：未超时，2：超时")
	private Integer waterTimeOut;

	/**
	 * 开票类型名称
	 */
	@ApiModelProperty(value = "开票类型名称")
	private String invoiceTypeName;

	/**
	 * 风险承诺函，多个图片之间用逗号分割
	 */
	@ApiModelProperty(value = "风险承诺函")
	private String riskCommitment;

	/**
	 * 风险承诺函
	 */
	@ApiModelProperty(value = "风险承诺函图片列表")
	private List<String> riskCommitmentList;

	/**
	 * 企业类型
	 */
	private Integer companyType;
}
