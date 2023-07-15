package com.yuqian.itax.user.entity;

import com.yuqian.itax.user.enums.MemberCompanyTypeEnum;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
/**
 * 我的企业
 *
 * @Date: 2019年12月06日 10:42:12
 * @author yejian
 */
@Getter
@Setter
@Table(name="t_e_member_company")
public class MemberCompanyEntity implements Serializable {

	private static final long serialVersionUID = -1L;

	/**
	 * 主键id
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@ApiModelProperty(value = "主键id")
	private Long id;

	/**
	 * 会员id
	 */
	@ApiModelProperty(value = "会员id")
	private Long memberId;

	/**
	 * 经营者名称
	 */
	@ApiModelProperty(value = "经营者名称")
	private String operatorName;

	/**
	 * 企业类型 1-个体开户 2-个独开户 3-有限合伙 4-有限责任
	 */
	@ApiModelProperty(value = "企业类型：1->个体开户；2->个独开户；3->有限合伙；4->有限责任")
	private Integer companyType;

	/**
	 * 公司名称
	 */
	@ApiModelProperty(value = "公司名称")
	private String companyName;

	/**
	 * 税号
	 */
	@ApiModelProperty(value = "税号")
	private String ein;

	/**
	 * 经营范围
	 */
	@ApiModelProperty(value = "经营范围")
	private String businessScope;

	/**
	 * 行业id
	 */
	@ApiModelProperty(value = "行业id")
	private Long industryId;

	/**
	 * 行业
	 */
	@ApiModelProperty(value = "行业")
	private String industry;

	/**
	 * 经营地址
	 */
	@ApiModelProperty(value = "经营地址")
	private String businessAddress;

	/**
	 * 营业执照
	 */
	@ApiModelProperty(value = "营业执照")
	private String businessLicense;

	/**
	 * 有效时间
	 */
	@ApiModelProperty(value = "有效时间")
	private Date endTime;

	/**
	 * 状态 1-正常 2-禁用  4-已税务注销 5-注销中 6-已工商注销
	 */
	private Integer status;

	/**
	 * 是否满额 0-否 1-是
	 */
	@ApiModelProperty(value = "是否满额 0->否 1->是")
	private Integer isTopUp;

	/**
	 * 年费
	 */
	@ApiModelProperty(value = "年费")
	private Long annualFee;

	/**
	 * 机构编码
	 */
	@ApiModelProperty(value = "机构编码")
	private String oemCode;

	/**
	 * 园区id
	 */
	@ApiModelProperty(value = "园区id")
	private Long parkId;

	/**
	 * 添加时间
	 */
	@ApiModelProperty(value = "添加时间")
	private Date addTime;

	/**
	 * 添加人
	 */
	@ApiModelProperty(value = " 添加人")
	private String addUser;

	/**
	 * 修改时间
	 */
	@ApiModelProperty(value = "修改时间")
	private Date updateTime;

	/**
	 * 修改人
	 */
	@ApiModelProperty(value = "修改人")
	private String updateUser;

	/**
	 * 备注
	 */
	@ApiModelProperty(value = "备注")
	private String remark;

	/**
	 * 经营者手机号
	 */
	@ApiModelProperty(value = "经营者手机号")
	private String operatorTel;

	/**
	 * 经营者邮箱
	 */
	@ApiModelProperty(value = "经营者邮箱")
	private String operatorEmail;

	/**
	 * 经办人账号
	 */
	@ApiModelProperty(value = "经办人账号")
	private String agentAccount;

	/**
	 * 身份证号码
	 */
	@ApiModelProperty(value = "身份证号码")
	private String idCardNumber;

	/**
	 * 身份证正面
	 */
	@ApiModelProperty(value = "身份证正面")
	private String idCardFront;

	/**
	 * 身份证反面
	 */
	@ApiModelProperty(value = "身份证反面")
	private String idCardReverse;

	/**
	 * 是否为他人办理 0-本人办理 1-为他人办理
	 */
	@ApiModelProperty(value = "是否为他人办理 0->本人办理 1->为他人办理")
	private Integer isOther;

	/**
	 * 营业执照副本
	 */
	private String businessLicenseCopy;
	/**
	 * 是否已达标 0-未达标 1-已达标
	 */
	private int isCompleted;

	/**
	 * 佣金默认开票主体 0-否 1-是
	 */
	private Integer commissionInvoiceDefault;

	/**
	 * 订单号
	 */
	private String orderNo;

	/**
	 * 过期状态 1-正常 2-即将过期  3-已过期
	 */
	private Integer overdueStatus;

	/**
	 * 是否发送过期提醒通知 0-未发送 1-已发送
	 */
	private Integer isSendNotice;
	/**
	 * 注销凭证
	 */
	private String cancelCredentials;
	/**
	 * 税务登记日期
	 */
	private Date taxRegDate;

	/**
	 * 注销说明
	 */
	private String cancelRemark;

	/**
	 * 注册签名图片
	 */
	private String signImg;

	/**
	 * 注册资本（万元）
	 */
	@ApiModelProperty(value = " 注册资本（万元）")
	private BigDecimal registeredCapital;

	/**
	 * 核定经营额(分）
	 */
	@ApiModelProperty(value = "核定经营额(分）")
	private Long approvedTurnover;

	/**
	 * 委托注册协议图片,多个文件采用逗号分隔 ,
	 */
	@ApiModelProperty(value = "委托注册协议图片")
	private String userAgreementImgs;

	/**
	 * 纳税人类型  1-小规模纳税人 2-一般纳税人
	 */
	@ApiModelProperty(value = "纳税人类型  1-小规模纳税人 2-一般纳税人")
	private Integer taxpayerType;

	/**
	 * 获取税务监控字典表编码
	 * @return
	 */
	public String getTaxMonitoringDictCode() {
		if (MemberCompanyTypeEnum.INDIVIDUAL.getValue().equals(this.getCompanyType())) {
			return "tax_monitoring_individual";
		} else if (MemberCompanyTypeEnum.INDEPENDENTLY.getValue().equals(this.getCompanyType())) {
			return "tax_monitoring_independently";
		} else if (MemberCompanyTypeEnum.LIMITED_PARTNER.getValue().equals(this.getCompanyType())) {
			return "tax_monitoring_limited_partner";
		} else {
			return "tax_monitoring_limited_liability";
		}
	}
}