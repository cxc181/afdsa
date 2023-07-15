package com.yuqian.itax.user.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;
/**
 * 会员账号表
 * 
 * @Date: 2019年12月06日 10:48:28 
 * @author Kaven
 */
@Getter
@Setter
@Table(name="t_e_member_account")
public class MemberAccountEntity implements Serializable {
	
	private static final long serialVersionUID = -1L;
	
	/**
	 * 主键id
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	/**
	 * 会员账号
	 */
	private String memberAccount;

	/**
	 * 会员头像
	 */
	private String headImg;

	/**
	 * 会员昵称
	 */
	private String memberName;

	/**
	 * 用户名
	 */
	private String realName;

	/**
	 * 实名认证状态 0-未认证 1-认证成功 2-认证失败
	 */
	private Integer authStatus;

	/**
	 * 身份证号码
	 */
	private String idCardNo;

	/**
	 * 身份证正面照地址
	 */
	private String idCardFront;

	/**
	 * 身份证反面照地址
	 */
	private String idCardBack;

	/**
	 * 身份证有效期
	 */
	private String expireDate;

	/**
	 * 身份证地址
	 */
	private String idCardAddr;

	/**
	 * openId
	 */
	private String openId;

	/**
	 * alipayUserId
	 */
	private String alipayUserId;// 支付宝用户ID，支付时需要

	/**
	 * 会员手机号
	 */
	private String memberPhone;
	
	/**
	 * 密码
	 */
	private String password;
	
	/**
	 * slat
	 */
	private String slat;
	
	/**
	 * 机构编码
	 */
	private String oemCode;
	
	/**
	 * 会员等级
	 */
	private Long memberLevel;
	
	/**
	 * 会员等级名称
	 */
	private String levelName;
	
	/**
	 * 状态  1-正常 0-禁用 2-注销
	 */
	private Integer status;
	
	/**
	 * 邀请人id
	 */
	private Long parentMemberId;
	
	/**
	 * 邀请人账号
	 */
	private String parentMemberAccount;
	
	/**
	 * 层级树
	 */
	private String memberTree;
	
	/**
	 * 所在省份
	 */
	private String provinceCode;
	
	/**
	 * 所在城市
	 */
	private String cityCode;
	
	/**
	 * 会员类型  1-会员 2-员工 
	 */
	private Integer memberType;
	
	/**
	 * 支付密码
	 */
	private String payPassword;
	
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
	 * 邀请码
	 */
	private String inviteCode;

	/**
	 * 邀请员工上限
	 */
	private Integer employeesLimit;

	/**
	 * 推广类型 1-散客 2-直客 3-顶级直客
	 */
	private Integer extendType;

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
	 * 是否付费升级 0-否 1-是
	 */
	private Integer isPayUpgrade;

	private Integer sourceType;// 操作小程序来源 1-微信小程序 2-支付宝小程序
	/**
	 * 会员标签 0-普通 1-海星
	 */
	private Integer sign;

	/**
	 * 最近开票电子邮箱（收票邮箱）
	 */
	private String email;
	/**
	 * 渠道服务商id
	 */
	private Long channelServiceId;
	/**
	 * 渠道员工id
	 */
	private Long channelEmployeesId;
	/**
	 * 渠道编码
	 */
	private String channelCode;
	/**
	 * 渠道产品编码
	 */
	private String channelProductCode;

	/**
	 * 是否手动降级 0-未降级 1-已手动降级
	 */
	private Integer isDemotion;

	/**
	 * 会员身份类型 0-未知 1-个人 2-企业
	 */
	private Integer memberAuthType;

	/**
	 * 渠道用户id
	 */
	private Long channelUserId;

	/**
	 * 实名推送状态推送状态：0-待推送 1-推送中 2-已推送 3-推送失败 4-无需推送
	 */
	private Integer authPushState;

//	/**
//	 * 达标个体数
//	 */
//	private Integer completeRegistCompanyNum;
//
//	/**
//	 * 达标开票数
//	 */
//	private Integer completeInvoiceNum;

	/**
	 * 接入方id
	 */
	private Long accessPartyId;
}

