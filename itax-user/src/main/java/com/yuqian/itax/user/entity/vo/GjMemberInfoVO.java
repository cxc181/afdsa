package com.yuqian.itax.user.entity.vo;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 会员账号表
 * 
 * @Date: 2019年12月06日 10:48:28 
 * @author Kaven
 */
@Getter
@Setter
public class GjMemberInfoVO implements Serializable {

	private static final long serialVersionUID = -1L;

	/**
	 * 主键id
	 */
	private Long id;

	/**
	 * 会员名称
	 */
	private String memberName;

	/**
	 * 渠道用户id
	 */
	private Long channelUserId;

	/**
	 * 会员账号
	 */
	private String memberAccount;

	/**
	 * 会员等级
	 */
	private Long levelNo;

	/**
	 * 会员等级名称
	 */
	private String levelName;

	/**
	 * 机构编码
	 */
	private String oemCode;

	/**
	 * 渠道服务商id
	 */
	private Long channelServiceId;

	/**
	 * 渠道员工id
	 */
	private Long channelEmployeesId;

	/**
	 * 渠道code
	 */
	private String channelCode;

	/**
	 * 产品code
	 */
	private String channelProductCode;

	/**
	 * 状态  1-正常 0-禁用 2-注销
	 */
	private Integer status;
}
