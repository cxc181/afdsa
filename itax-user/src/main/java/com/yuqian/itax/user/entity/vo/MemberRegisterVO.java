package com.yuqian.itax.user.entity.vo;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

/**
 *  @Author: Kaven
 *  @Date: 2020/3/25 9:55
 *  @Description: 用户注册信息返回bean
 */
@Getter
@Setter
public class MemberRegisterVO implements Serializable {

	private static final long serialVersionUID = -1L;

	/**
	 * 注册手机号
	 */
	private String regPhone;

	/**
	 * 用户名
	 */
	private String userName;

	/**
	 * 用户真实姓名
	 */
	private String realName;

	/**
	 * 身份证号
	 */
	private String idCardNo;

	/**
	 * 注册时间
	 */
	private Date regsiterTime;

	/**
	 * 邀请码
	 */
	private String inviteCode;

	/**
	 * 账户状态
	 */
	private String status;

	/**
	 * 所在省编码
	 */
	private String provinceCode;

	/**
	 * 所在省名称
	 */
	private String provinceName;

	/**
	 * 所在市编码
	 */
	private String cityCode;

	/**
	 * 所在市名称
	 */
	private String cityName;

}
