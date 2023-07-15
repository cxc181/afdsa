package com.yuqian.itax.user.entity.vo;

import com.alibaba.fastjson.annotation.JSONField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

/**
 * 我的企业
 *
 * @author yejian
 * @Date: 2020年07月15日 15:42:12
 */
@Getter
@Setter
public class CompanyListApitVO implements Serializable {

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
	 * 企业类型 1-个体户 2-个人独资企业 3-有限合伙公司 4-有限责任公司
	 */
	@ApiModelProperty(value = "状态：1->个体户；2->个人独资企业；3->有限合伙公司；4->有限责任公司")
	private Integer companyType;

	/**
	 * 有效截止时间
	 */
	@ApiModelProperty(value = "有效截止时间")
	@JSONField(format = "yyyy-MM-dd")
	private Date endTime;

	/**
	 * 经营者手机号
	 */
	@ApiModelProperty(value = "经营者手机号")
	private String regPhone;

	/**
	 * 经营者名称
	 */
	@ApiModelProperty(value = "经营者名称")
	private String operatorName;

	/**
	 * 园区编码
	 */
	@ApiModelProperty(value = "园区编码")
	private String parkCode;

	/**
	 * 园区名称
	 */
	@ApiModelProperty(value = "园区名称")
	private String parkName;

	/**
	 * 状态 1-正常 2-禁用 4-已注销 5-注销中
	 */
	@ApiModelProperty(value = "状态：1->正常；2->禁用 4->已注销 5->注销中")
	private Integer status;

	/**
	 * 年度总开票金额
	 */
	@ApiModelProperty(value = "年度总开票金额")
	private Long totalInvoiceAmount;

	/**
	 * 年度已开票金额
	 */
	@ApiModelProperty(value = "年度已开票金额")
	private Long useInvoiceAmount;

	/**
	 * 年度可开票金额
	 */
	@ApiModelProperty(value = "年度可开票金额")
	private Long remainInvoiceAmount;

	/**
	 * 个人所得税减免额度
	 */
	@ApiModelProperty(value = "个人所得税减免额度")
	private Long incomeTaxBreaksAmount;

	/**
	 * 个人所得税减免周期 1-按月 2-按季度
	 */
	@ApiModelProperty(value = "个人所得税减免周期 1-按月 2-按季度")
	private Long incomeTaxBreaksCycle;

	/**
	 * 个人所得税减免剩余额度
	 */
	@ApiModelProperty(value = "个人所得税减免剩余额度")
	private Long incomeTaxBreaksRemainAmount;

	/**
	 * 增值税减免额度
	 */
	@ApiModelProperty(value = "增值税减免额度")
	private Long vatBreaksAmount;

	/**
	 * 增值税减免周期 1-按月 2-按季度
	 */
	@ApiModelProperty(value = "增值税减免周期 1-按月 2-按季度")
	private Integer vatBreaksCycle;

	/**
	 * 增值税减免剩余额度
	 */
	@ApiModelProperty(value = "增值税减免剩余额度")
	private Long vatBreaksRemainAmount;

}
