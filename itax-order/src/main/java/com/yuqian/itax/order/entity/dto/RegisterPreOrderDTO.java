package com.yuqian.itax.order.entity.dto;

import com.yuqian.itax.user.entity.CompanyCorePersonnelEntity;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 *  @Author: lmh
 *  @Date: 2022/6/28
 *  @Description: 工商注册预订单创建接收实体类
 */
@Getter
@Setter
public class RegisterPreOrderDTO implements Serializable {

	private static final long serialVersionUID = -1L;

	/**
	 * 主键id
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	/**
	 * 会员id
	 */
	private Long memberId;

	/**
	 * 订单号
	 */
	private String orderNo;

	/**
	 * 机构编码
	 */
	private String oemCode;

	/**
	 * 法人姓名
	 */
	private String operatorName;

	/**
	 * 联系电话
	 */
	private String contactPhone;

	/**
	 * 电子邮箱
	 */
	private String email;

	/**
	 * 身份证正面
	 */
	private String idCardFront;

	/**
	 * 身份证反面
	 */
	private String idCardReverse;

	/**
	 * 身份证号码
	 */
	private String idCardNumber;

	/**
	 * 身份证地址
	 */
	private String idCardAddr;

	/**
	 * 身份证有效期，格式如2010/01/01-2020/01/01
	 */
	private String expireDate;

	/**
	 * 字号
	 */
	private String shopName;

	/**
	 * 备选字号1
	 */
	private String shopNameOne;

	/**
	 * 备选字号2
	 */
	private String shopNameTwo;

	/**
	 * 行业类型id
	 */
	private Long industryId;

	/**
	 * 核定税种
	 */
	private String ratifyTax;

	/**
	 * 经营范围
	 */
	private String businessScope;

	/**
	 * 注册名称
	 */
	private String registeredName;

	/**
	 * 备注
	 */
	private String remark;

	/**
	 * 企业类型 1-个体工商户 2-个人独资企业 3-有限合伙 4-有限责任
	 */
	private Integer companyType;

	/**
	 * 是否为他人办理 0-本人办理 1-为他人办理
	 */
	private Integer isOther;

	/**
	 * 注册资本（万元）
	 */
	@NotNull(message = "注册资本不能为空")
	private BigDecimal registeredCapital;

	/**
	 * 纳税人类型  1-小规模纳税人 2-一般纳税人
	 */
	private Integer taxpayerType;

	/**
	 * 财务身份信息
	 */
	private CompanyCorePersonnelEntity financeInfo;

	/**
	 * 监事身份信息
	 */
	private CompanyCorePersonnelEntity supervisorInfo;

	/**
	 * 操作用户
	 */
	private String addUser;

	@NotNull(message = "请传入产品ID")
	private Long productId;// 产品ID

	@NotNull(message = "请传入园区ID")
	private Long parkId;// 园区ID
}
