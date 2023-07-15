package com.yuqian.itax.user.entity.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 我的企业
 * 
 * @Date: 2019年12月06日 10:42:12 
 * @author yejian
 */
@Getter
@Setter
public class MemberCompanyDetailVo implements Serializable {
	
	private static final long serialVersionUID = -1L;
	
	/**
	 * 主键id
	 */
	@ApiModelProperty(value = "主键id")
	private Long id;

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
	 * 年度可开票金额
	 */
	@ApiModelProperty(value = "年度可开票金额")
	private Long remainInvoiceAmount;

	/**
	 * 企业类型 1-个体户 2-个人独资企业 3-有限合伙公司 4-有限责任公司
	 */
	@ApiModelProperty(value = "状态：1->个体户；2->个人独资企业；3->有限合伙公司；4->有限责任公司")
	private Integer companyType;

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
	 * 园区编码
	 */
	@ApiModelProperty(value = "园区编码")
	private String parkCode;

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
	 * 累积开票金额
	 */
	@ApiModelProperty(value = "累积开票金额")
	private Long useInvoiceAmount;

	/**
	 * 注销服务费
	 */
	@ApiModelProperty(value = "注销服务费")
	private Long cancelFee;

	/**
	 * 注册时间
	 */
	@ApiModelProperty(value = "注册时间")
	private Date addTime;


	/**
	 * 产品名称
	 */
	@ApiModelProperty(value = "产品名称")
	private String prodName;

	/**
	 * 注销累计开票额度
	 */
	@ApiModelProperty(value = "注销累计开票额度")
	private Long cancelTotalLimit;


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
	 * 税号
	 */
	private String ein;

	/**
	 * 托管费到期日
	 */
	private String endTime;

	/**
	 * 重置日期
	 */
	private String resetDate;

	/**
	 * 本年累计开票
	 */
	private Long useInvoiceAmountYear = 0L;

	/**
	 * 年度开票总额（分）
	 */
	private Long totalInvoiceAmount = 0L;

	/**
	 * 托管状态 0-未托管 1-已托管
	 */
	private Integer manageStatus;

	/**
	 * 企业状态
	 */
	private Integer status;

	/**
	 * 企业过期状态
	 */
	private Integer overdueStatus;

//	public void setResetDate(String resetDate) {
//		if (StringUtils.isBlank(resetDate)) {
//			return;
//		}
//		Date date = DateUtil.parseTimesTampDate(resetDate);
//		this.resetDate = DateUtil.formatDefaultDate(date);
//	}

	/**
	 * 特价活动产品id
	 */
	private Long discountActivityId;

	/**
	 * 委托注册协议
	 */
	private String registrationAgreement;

	/**
	 * 协议模板名称
	 */
	private String templateName;

	/**
	 * 注销说明
	 */
	private String cancelRemark;

	/**
	 * 本季已开票金额
	 */
	private Long periodInvoiceAmount;

	/**
	 * 季度限额提醒金额
	 */
	private Long quotaWarnAmount;

	/**
	 * 注册资本(万元)
	 */
	private BigDecimal registeredCapital;

	/**
	 * 核定经营额（分）
	 */
	private Long approvedTurnover;

	/**
	 * 是否存在可用续费产品 0-否 1-是
	 */
	private Integer isExistRenewProduct;

	/**
	 * 纳税人类型 1-小规模纳税人 2-一般纳税人
	 */
	private Integer taxpayerType;

	/**
	 * 委托注册协议图片
	 */
	private String userAgreementImgs;

	/**
	 * 委托注册协议图片集合
	 */
	private List<String> userAgreementImgsList;

	/**
	 * 续费产品状态 0-待上架 1-已上架 2-已下架 3-已暂停
	 */
	private Integer renewProductStatus;

	/**
	 * 客服电话
	 */
	private String customerServiceTel;
}
