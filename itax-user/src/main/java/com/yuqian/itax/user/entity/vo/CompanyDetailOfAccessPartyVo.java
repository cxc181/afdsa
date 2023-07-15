package com.yuqian.itax.user.entity.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

/**
 * @ClassName CompanyDetailOfAccessPartyVo
 * @Description 接入方企业详情返回
 * @Author lmh
 * @Date 2021/8/9 14:23
 * @Version 1.0
 */
@Getter
@Setter
public class CompanyDetailOfAccessPartyVo implements Serializable {
	
	private static final long serialVersionUID = -1L;

	/**
	 * 经营者名称
	 */
	@ApiModelProperty(value = "经营者名称")
	private String operatorName;

	/**
	 * 会员手机号
	 */
	@ApiModelProperty(value = "会员手机号")
	private String memberPhone;

	/**
	 * 公司名称
	 */
	@ApiModelProperty(value = "公司名称")
	private String companyName;

	/**
	 * 企业类型 1-个体户 2-个人独资企业 3-有限合伙公司 4-有限责任公司
	 */
	@ApiModelProperty(value = "状态：1->个体户；2->个人独资企业；3->有限合伙公司；4->有限责任公司")
	private String companyTypeName;

	/**
	 * 园区名称
	 */
	@ApiModelProperty(value = "园区名称")
	private String parkName;

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
	 * 个人所得税优惠政策
	 */
	@ApiModelProperty(value = "个人所得税优惠政策")
	private Long incomeTaxBreaksAmount;

	/**
	 * 个人所得税减免周期 1-按月 2-按季度
	 */
	@ApiModelProperty(value = "个人所得税减免周期 1-按月 2-按季度")
	private Long incomeTaxBreaksCycle;

	/**
	 * 营业执照
	 */
	@ApiModelProperty(value = "营业执照")
	private String businessLicense;

	/**
	 * 营业执照副本
	 */
	@ApiModelProperty(value = "营业执照副本")
	private String businessLicenseCopy;

	/**
	 * 证件列表
	 */
	@ApiModelProperty(value = "证件列表")
	List<MemberCompanyCertListVo> certList;

	/**
	 * 注销凭证
	 */
	@ApiModelProperty(value = "注销凭证")
	private String cancelCredentials;

	/**
	 * 托管费到期日
	 */
	private String endTime;

	/**
	 * 企业状态
	 */
	private Integer status;

	/**
	 * 企业过期状态
	 */
	private Integer overdueStatus;
}
