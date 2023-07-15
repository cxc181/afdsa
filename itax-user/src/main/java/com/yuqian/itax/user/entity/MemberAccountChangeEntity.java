package com.yuqian.itax.user.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;
/**
 * 会员账号变动表
 * 
 * @Date: 2021年02月03日 11:25:34 
 * @author 蒋匿
 */
@Getter
@Setter
@Table(name="t_e_member_account_change")
public class MemberAccountChangeEntity implements Serializable {
	
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
	private Long accountId;
	
	/**
	 * 会员账号
	 */
	private String memberAccount;
	
	/**
	 * 会员昵称
	 */
	private String memberName;
	
	/**
	 * 头像
	 */
	private String headImg;
	
	/**
	 * 用户名
	 */
	private String realName;
	
	/**
	 * 支付宝userId，支付宝支付时需要
	 */
	private String alipayUserId;
	
	/**
	 * 微信openId，微信支付时需要
	 */
	private String openId;
	
	/**
	 * 会员手机号
	 */
	private String memberPhone;
	
	/**
	 * 新手机号
	 */
	private String phoneNew;
	
	/**
	 * 最近开票电子邮箱
	 */
	private String email;
	
	/**
	 * 机构编码
	 */
	private String oemCode;
	
	/**
	 * 会员等级id
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
	 * 身份证地址
	 */
	private String idCardAddr;
	
	/**
	 * 实名认证状态 0-未认证 1-认证成功 2-认证失败
	 */
	private Integer authStatus;
	
	/**
	 * 身份证有效期，格式如2010/01/01-2020/01/01
	 */
	private String expireDate;
	
	/**
	 * 邀请码
	 */
	private String inviteCode;
	
	/**
	 * 钻石会员员工上限
	 */
	private Integer employeesLimit;
	
	/**
	 * 推广角色 1-散客 2-直客 3-顶级直客
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
	 * 上级钻石会员id
	 */
	private Long upDiamondId;
	
	/**
	 * 上级钻石会员账号
	 */
	private String upDiamondAccount;
	
	/**
	 * 上上级钻石会员id
	 */
	private Long superDiamondId;
	
	/**
	 * 上上级钻石会员账号
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
	 * 是否付费升级 0-否 1-是
	 */
	private Integer isPayUpgrade;
	
	/**
	 * 操作小程序来源 1-微信小程序 2-支付宝小程序
	 */
	private Integer sourceType;
	/**
	 * 会员标签 0-普通 1-海星
	 */
	private Integer sign;
	/**
	 * 附件地址
	 */
	private String fileUrl;

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

}
