package com.yuqian.itax.user.entity.vo;

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
 * 会员账号等级分润
 */
@Getter
@Setter
public class MemberAccountLevelProfitsRuleVO implements Serializable {
	
	private static final long serialVersionUID = -1L;
	
	/**
	 * 主键id
	 */
	private Long id;

	/**
	 * 会员账号
	 */
	private String memberAccount;

	/**
	 * 会员名称
	 */
	private String memberName;

	/**
	 * 会员手机号
	 */
	private String memberPhone;

	/**
	 * 邀请人id
	 */
	private Long parentMemberId;

	/**
	 * 邀请人账号
	 */
	private String parentMemberAccount;

	/**
	 * 会员类型  1-会员 2-员工 
	 */
	private Integer memberType;

	/**
	 * 所属员工id
	 */
	private Long attributionEmployeesId;

	/**
	 * 所属员工账号
	 */
	private String attributionEmployeesAccount;

	/**
	 * 上上级员工id
	 */
	private Long superEmployeesId;

	/**
	 * 上上级员工账号
	 */
	private String superEmployeesAccount;

	/**
	 * 上级城市服务商id
	 */
	private Long upDiamondId;

	/**
	 * 上级城市服务商账号
	 */
	private String upDiamondAccount;

	/**
	 * 上上级城市服务商id
	 */
	private Long superDiamondId;

	/**
	 * 上上级城市服务商账号
	 */
	private String superDiamondAccount;

	/**
	 * 等级标识 -1-员工 0-普通会员  1-VIP 2-白银会员 3-税务顾问 4-铂金会员 5-城市服务商
	 */
	private Integer levelNo;

	/**
	 * 会费分润
	 */
	private BigDecimal membershipFee;

	/**
	 * 下级是城市服务商时会费分润率
	 */
	private BigDecimal profitsDiamondTwoMembershipFee;

	/**
	 * 托管费分润率
	 */
	private BigDecimal profitsEntrustFeeRate;

	/**
	 * 下级是城市服务商时托管费分润率
	 */
	private BigDecimal profitsDiamondTwoEntrustFeeRate;

	/**
	 * 服务费分润率
	 */
	private BigDecimal serviceFeeRate;

	/**
	 * 下级是城市服务商时服务费分润率
	 */
	private BigDecimal profitsDiamondTwoServiceFeeRate;

	/**
	 * 消费折扣
	 */
	private BigDecimal consumptionDiscount;

}

