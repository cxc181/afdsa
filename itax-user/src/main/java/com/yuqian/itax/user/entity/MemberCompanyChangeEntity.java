package com.yuqian.itax.user.entity;

import io.swagger.annotations.ApiModelProperty;
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
 * 企业变更表
 * 
 * @Date: 2021年06月04日 09:31:10 
 * @author 蒋匿
 */
@Getter
@Setter
@Table(name="t_e_member_company_change")
public class MemberCompanyChangeEntity implements Serializable {
	
	private static final long serialVersionUID = -1L;
	
	/**
	 * 主键id
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	/**
	 * 企业id
	 */
	private Long companyId;
	
	/**
	 * 会员id
	 */
	private Long memberId;
	
	/**
	 * 公司名称
	 */
	private String companyName;
	
	/**
	 * 税号
	 */
	private String ein;
	
	/**
	 * 经营范围
	 */
	private String businessScope;
	
	/**
	 * 行业类型id
	 */
	private Long industryId;
	
	/**
	 * 行业
	 */
	private String industry;
	
	/**
	 * 经营地址
	 */
	private String businessAddress;
	
	/**
	 * 营业执照
	 */
	private String businessLicense;
	
	/**
	 * 营业执照副本
	 */
	private String businessLicenseCopy;
	
	/**
	 * 有效时间
	 */
	private Date endTime;
	
	/**
	 * 状态 1-正常 2-禁用 4-已注销 5-注销中 
	 */
	private Integer status;
	
	/**
	 * 年费
	 */
	private Long annualFee;
	
	/**
	 * 机构编码
	 */
	private String oemCode;
	
	/**
	 * 园区id
	 */
	private Long parkId;
	
	/**
	 * 是否满额 0-否 1-是
	 */
	private Integer isTopUp;
	
	/**
	 * 添加时间
	 */
	private Date addTime;
	
	/**
	 * 添加人
	 */
	private String addUser;
	
	/**
	 * 修改时间
	 */
	private Date updateTime;
	
	/**
	 * 修改人
	 */
	private String updateUser;
	
	/**
	 * 备注
	 */
	private String remark;
	
	/**
	 * 经营者名称
	 */
	private String operatorName;
	
	/**
	 * 企业类型1-个体户 2-个人独资企业 3-有限合伙 4-有限责任
	 */
	private Integer companyType;
	
	/**
	 * 经营者手机号
	 */
	private String operatorTel;
	
	/**
	 * 经营者邮箱
	 */
	private String operatorEmail;
	
	/**
	 * 经办人账号
	 */
	private String agentAccount;
	
	/**
	 * 身份证号码
	 */
	private String idCardNumber;
	
	/**
	 * 身份证正面
	 */
	private String idCardFront;
	
	/**
	 * 身份证反面
	 */
	private String idCardReverse;
	
	/**
	 * 是否为他人办理 0-本人办理 1-为他人办理
	 */
	private Integer isOther;
	
	/**
	 * 佣金默认开票主体 0-否 1-是
	 */
	private Integer commissionInvoiceDefault;
	
	/**
	 * 订单号
	 */
	private String orderNo;
	
	/**
	 * 过期状态 1-正常 2-即将过期 3-已过期
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
	 * 注册资本（万元）
	 */
	@ApiModelProperty(value = " 注册资本（万元）")
	private BigDecimal registeredCapital;

	/**
	 * 核定经营额(分）
	 */
	@ApiModelProperty(value = "核定经营额(分）")
	private Long approvedTurnover;
}
