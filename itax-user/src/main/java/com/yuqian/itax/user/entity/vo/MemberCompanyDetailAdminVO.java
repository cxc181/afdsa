package com.yuqian.itax.user.entity.vo;

import com.yuqian.itax.system.entity.RatifyTaxEntity;
import com.yuqian.itax.user.entity.CompanyInvoiceCategoryEntity;
import com.yuqian.itax.user.entity.CompanyResourcesAddressEntity;
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
public class MemberCompanyDetailAdminVO implements Serializable {

	private static final long serialVersionUID = -1L;

	/**
	 * 主键id
	 */
	private Long id;

	/**
	 * 公司名称
	 */
	private String companyName;

	/**
	 * 企业类型 1-个体户 2-个人独资企业 3-有限合伙公司 4-有限责任公司
	 */
	private Integer companyType;

	/**
	 * 身份证号码
	 */
	private String idCardNumber;

	/**
	 * 所属园区
	 */
	private String parkName;

	/**
	 * 出证时间
	 */
	private Date addTime;

	/**
	 * 经营者名称
	 */
	private String memberPhone;

	/**
	 * 经营者名称
	 */
	private String operatorName;

	/**
	 * 经营者联系方式
	 */
	private String operatorTel;

	/**
	 * 经营者邮箱
	 */
	private String operatorEmail;

	/**
	 * 状态 1-正常 2-禁用  3-过期 4-已过期
	 */
	private Integer status;

	/**
	 * 年度开票限额
	 */
	private Long totalInvoiceAmount;

	/**
	 * 近12个月已开票金额
	 */
	private Long useInvoiceAmount;

	/**
	 * 近12个月可开票金额
	 */
	private Long remainInvoiceAmount;

	/**
	 * 年费到期时间
	 */
//	@JSONField(format = "yyyy-MM-dd")
	private Date endTime;

	/**
	 * 行业id
	 */
	private Long industryId;

	/**
	 * 行业
	 */
	private String industry;

	/**
	 * 核定税种
	 */
	private List<RatifyTaxEntity> ratifies;

	/**
	 * 经办人账号
	 */
	private String agentAccount;

	/**
	 * 开票类目
	 */
	private List<CompanyInvoiceCategoryEntity> categories;

	/**
	 * 开票类目（字符串）
	 */
	private String categoriesString;

	/**
	 * 经营范围
	 */
	private String businessScope;

	/**
	 * 经营地址
	 */
	private String businessAddress;

	/**
	 * 机构编码
	 */
	private String oemCode;

	/**
	 * 所属OEM
	 */
	private String oemName;

	/**
	 * 营业执照
	 */
	private String businessLicense;
	/**
	 * 营业执照
	 */
	private String businessLicenseFileName;

	/**
	 * 证件所在地
	 */
	private List<CompanyResourcesAddressEntity> addresses;

	/**
	 * 营业执照(副本)
	 */
	private String businessLicenseCopy;

	/**
	 * 营业执照(副本)（短）
	 */
	private String businessLicenseCopyFileName;
	/**
	 * 税号
	 */
	private String ein;
	/**
	 * 注销凭证
	 */
	private String cancelCredentials;
	/**
	 * 税务登记日期
	 */
	private String taxRegDate;
	/**
	 * 注销说明
	 */
	private String cancelRemark;

	/**
	 * 注册资本（万元）
	 */
	private BigDecimal registeredCapital;

	/**
	 * 核定经营额(分）
	 */
	private Long approvedTurnover;

	/**
	 * 委托注册协议图片, 用逗号分隔
	 */
	private String userAgreementImgs;

	/**
	 * 委托注册协议图片授权集合
	 */
	private List<String> userAgreementImgList;

	/**
	 * 纳税人类型  1-小规模纳税人 2-一般纳税人
	 */
	private Integer taxpayerType;

	/**
	 * 企业成员集合
	 */
	List<CompanyCorePersonnelVO> companyCorePersonnelList;
}