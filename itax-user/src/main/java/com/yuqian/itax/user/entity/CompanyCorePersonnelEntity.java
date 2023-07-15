package com.yuqian.itax.user.entity;

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
 * 企业核心成员信息表
 * 
 * @Date: 2022年06月27日 17:55:00 
 * @author 蒋匿
 */
@Getter
@Setter
@Table(name="t_e_company_core_personnel")
public class CompanyCorePersonnelEntity implements Serializable {
	
	private static final long serialVersionUID = -1L;
	
	/**
	 * 主键id
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	/**
	 * 订单号
	 */
	private String orderNo;
	
	/**
	 * 会员id
	 */
	private Long memberId;
	
	/**
	 * 企业id
	 */
	private Long companyId;
	
	/**
	 * 企业类型1-个体工商户 2-个人独资企业 3-有限合伙 4-有限责任
	 */
	private Integer companyType;
	
	/**
	 * 成员类型  1-经理 2- 监事 3-执行董事 4-财务 5-无职务   多个类型之间用逗号分割
	 */
	private String personnelType;

	/**
	 * 合伙人类型 1-普通合伙 2-有限合伙
	 */
	private Integer partnerType;

	/**
	 * 是否法人 0-否 1-是
	 */
	private Integer isLegalPerson;

	/**
	 * 是否执行事务合伙人 0-否 1-是
	 */
	private Integer isExecutivePartner;

	/**
	 * 委派方id
	 */
	private Long appointPartyId;

	/**
	 * 身份类型 1-自然人 2-企业
	 */
	private Integer identityType;

	/**
	 * 是否股东 0-否 1-是
	 */
	private Integer isShareholder;
	/**
	 * 姓名
	 */
	private String personnelName;
	
	/**
	 * 联系电话
	 */
	private String contactPhone;
	
	/**
	 * 证件号
	 */
	private String certificateNo;
	
	/**
	 * 证件地址
	 */
	private String certificateAddr;
	
	/**
	 * 证件有效期
	 */
	private String expireDate;
	
	/**
	 * 身份证正面
	 */
	private String idCardFront;
	
	/**
	 * 身份证反面
	 */
	private String idCardReverse;
	
	/**
	 * 企业营业执照
	 */
	private String businessLicense;
	
	/**
	 * 投资金额(万元)
	 */
	private BigDecimal investmentAmount;
	
	/**
	 * 占股比例(小数)
	 */
	private BigDecimal shareProportion;
	
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
	
	
}
