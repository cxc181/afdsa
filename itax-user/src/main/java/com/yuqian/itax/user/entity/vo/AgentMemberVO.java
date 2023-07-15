package com.yuqian.itax.user.entity.vo;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;


/**
 *  @Author: HZ
 *  @Description: 推广用户查询VO
 */
@Getter
@Setter
public class AgentMemberVO implements Serializable {

	private static final long serialVersionUID = -1L;
	/**
	 * 用户名
	 */
	private String memberName;
	/**
	 * 会员手机号
	 */
	private String memberPhone;
	/**
	 * 会员等级 0-普通会员  1-VIP
	 */
	private Integer levelNo;
	/**
	 * 会员等级 0-普通会员  1-VIP
	 */
	private String levelName;
	/**
	 * 注册时间
	 */
	private Date registTime;
	/**
	 * 用户账户状态，1、正常；2、禁用；3、注销
	 */
	private String status;
	/**
	 * 代理商id
	 */
	private Long channelServiceId;
	/**
	 * 员工id
	 */
	private Long channelEmployeesId;
	/**
	 * 是否裂变 0-直推 1-裂变
	 */
	private Integer isFission;
	/**
	 * 渠道来源
	 */
	private String channelCode;
	/**
	 * 产品编码
	 */
	private String channelProductCode;

	/**
	 * 用户id
	 */
	private Long memberId;

	/**
	 * 渠道用户映射id
	 */
	private Long channelUserId;
}
