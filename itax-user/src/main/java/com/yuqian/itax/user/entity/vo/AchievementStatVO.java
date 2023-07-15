package com.yuqian.itax.user.entity.vo;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;


/**
 *  @Author: Kaven
 *  @Date: 2020/6/5 15:00
 *  @Description: 业绩统计展示VO
 */
@Getter
@Setter
public class AchievementStatVO implements Serializable {

	private static final long serialVersionUID = -1L;
	/**
	 * 用户名
	 */
	private String userName;

	/**
	 * 推广角色类型 1-散客 2-直客 3-顶级直客
	 */
	private Integer extendType;

	/**
	 * 会员等级 0-普通会员  1-VIP 2-白银会员 3-税务顾问 4-铂金会员 5-城市服务商
	 */
	private Integer levelNo;

	/**
	 * 会员账号
	 */
	private String memberAccount;

	/**
	 * 用户类型 1-会员 2-系统用户
	 */
	private Integer userType;

	/**
	 * 升级描述
	 */
	private String upgradeDesc;

	/**
	 * 直推用户数
	 */
	private Long extendUserCount;

	/**
	 * 直推个体数
	 */
	private Long extendCompanyCount;

	/**
	 * 直推达标个体数
	 */
	private Long extendStdComCount;

	/**
	 * 累计直推开票
	 */
	private Long extendInvoiceAmount;

	/**
	 * 裂变用户数
	 */
	private Long fissionUserCount;

	/**
	 * 裂变个体数
	 */
	private Long fissionCompanyCount;

	/**
	 * 累计佣金
	 */
	private Long totalProfitsAmount;

	/**
	 * 新增直推用户
	 */
	private Long addExtendUserCount;

	/**
	 * 新增直推个体
	 */
	private Long addExtendCompanyCount;

	/**
	 * 新增开票金额
	 */
	private Long addInvoiceAmount;

	/**
	 * 新增佣金
	 */
	private Long addProfitsAmount;

	/**
	 * 新增裂变用户
	 */
	private Long addFissionUserCount;

	/**
	 * 新增裂变个体
	 */
	private Long addFissionCompanyCount;

	/**
	 * 新增裂变开票金额
	 */
	private Long addFissionInvoiceAmount;

	/**
	 * 新增裂变佣金
	 */
	private Long addFissionProfitsAmount;
}
