package com.yuqian.itax.order.entity;

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
 * 会员订单关系表
 * 
 * @Date: 2019年12月10日 12:19:24 
 * @author 蒋匿
 */
@Getter
@Setter
@Table(name="t_r_member_order")
public class MemberOrderRelaEntity implements Serializable {
	
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
	 * 会员等级
	 */
	private Integer memberLevel;

	/**
	 * 一级推广人账号id
	 */
	private Long accountFirstId;

	/**
	 * 一级推广人账号
	 */
	private String accountFirst;

	/**
	 * 一级推广人手机号码
	 */
	private String phoneFirst;

	/**
	 * 一级推广人名称
	 */
	private String nameFirst;

	/**
	 * 一级推广人等级
	 */
	private Integer levelFirst;

	/**
	 * 一级推广人分润比率
	 */
	private BigDecimal levelFirstProfitsRate;

	/**
	 * 二级推广人账号id
	 */
	private Long accountTwoId;

	/**
	 * 二级推广人账号
	 */
	private String accountTwo;

	/**
	 * 二级推广人手机号码
	 */
	private String phoneTwo;

	/**
	 * 二级推广人名称
	 */
	private String nameTwo;

	/**
	 * 二级推广人等级
	 */
	private Integer levelTwo;

	/**
	 * 二级推广人分润比率
	 */
	private BigDecimal levelTwoProfitsRate;

	/**
	 * 三级推广人账号id
	 */
	private Long accountThreeId;

	/**
	 * 三级推广人账号
	 */
	private String accountThree;

	/**
	 * 三级推广人手机号码
	 */
	private String phoneThree;

	/**
	 * 三级推广人名称
	 */
	private String nameThree;

	/**
	 * 三级推广人等级
	 */
	private Integer levelThree;

	/**
	 * 三级推广人分润比率
	 */
	private BigDecimal levelThreeProfitsRate;

	/**
	 * 四级推广人账号id
	 */
	private Long accountFourId;

	/**
	 * 四级推广人账号
	 */
	private String accountFour;

	/**
	 * 四级推广人手机号码
	 */
	private String phoneFour;

	/**
	 * 四级推广人名称
	 */
	private String nameFour;

	/**
	 * 四级推广人等级
	 */
	private Integer levelFour;

	/**
	 * 四级推广人分润比率
	 */
	private BigDecimal levelFourProfitsRate;

	/**
	 * 城市合伙人id
	 */
	private Long cityProvidersId;

	/**
	 * 城市合伙人
	 */
	private String cityProviders;

	/**
	 * 城市合伙人手机号码
	 */
	private String cityProvidersPhone;

	/**
	 * 城市合伙人名称
	 */
	private String cityProvidersName;

	/**
	 * 城市合伙人分润比率
	 */
	private BigDecimal cityProvidersProfitsRate;

	/**
	 * 高级城市合伙人id
	 */
	private Long cityPartnerId;

	/**
	 * 高级城市合伙人
	 */
	private String cityPartner;

	/**
	 * 高级城市合伙人手机号码
	 */
	private String cityPartnerPhone;

	/**
	 * 高级城市合伙人名称
	 */
	private String cityPartnerName;

	/**
	 * 高级城市合伙人分润比率
	 */
	private BigDecimal cityPartnerProfitsRate;

	/**
	 * 平台账号id
	 */
	private Long platformAccountId;

	/**
	 * 平台账号
	 */
	private String platformAccount;
	
	/**
	 * 平台分润比率
	 */
	private BigDecimal platformAccountProfitsRate;
	
	/**
	 * 机构编码
	 */
	private String oemCode;
	
	/**
	 * 机构名称
	 */
	private String oemName;
	
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
	 * 所属员工id
	 */
	private Long attributionEmployeesId;

	/**
	 *  所属员工账号
	 */
	private String attributionEmployeesAccount;

	/**
	 *  上级城市服务商id
	 */
	private Long upDiamondId;

	/**
	 *  上级城市服务商账号
	 */
	private String upDiamondAccount;

	/**
	 *  上上级城市服务商id
	 */
	private Long superDiamondId;

	/**
	 * 上上级城市服务商账号
	 */
	private String superDiamondAccount;

	/**
	 * 上上级员工id
	 */
	private Long superEmployeesId;

	/**
	 * 上上级员工账号
	 */
	private String superEmployeesAccount;

	/**
	 * 邀请人id
	 */
	private Long parentMemberId;

	/**
	 * 邀请人账号
	 */
	private String parentMemberAccount;

	/**
	 * 邀请人等级标识 -1-员工 0-普通会员  1-VIP 2-白银会员 3-税务顾问 4-铂金会员 5-城市服务商
	 */
	private Integer parentMemberLevelNo;
}
