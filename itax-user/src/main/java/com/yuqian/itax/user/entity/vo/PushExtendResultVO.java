package com.yuqian.itax.user.entity.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

/**
 *  @Author: Kaven
 *  @Date: 2020/6/5 9:42
 *  @Description: 推广中心-直推用户信息展示VO
 */
@Getter
@Setter
public class PushExtendResultVO implements Serializable {
	
	private static final long serialVersionUID = -1L;

	/**
	 * 用户ID
	 */
	@ApiModelProperty(value = "用户ID")
	private Long userId;

	/**
	 * 用户姓名（优先显示实名，未实名显示昵称）
	 */
	@ApiModelProperty(value = "用户姓名（优先显示实名，未实名显示昵称）")
	private String memberName;

	/**
	 * 用户手机号
	 */
	@ApiModelProperty(value = "用户手机号")
	private String memberPhone;

	/**
	 * 直推用户数
	 */
	@ApiModelProperty(value = "直推用户数")
	private Long pushMemberCount = 0L;

	/**
	 * 直推个体数
	 */
	@ApiModelProperty(value = "直推个体数")
	private Long pushPersonalityCount = 0L;

	/**
	 * 本月佣金
	 */
	@ApiModelProperty(value = "本月佣金")
	private Long monthCommission = 0L;

	/**
	 * 本年佣金
	 */
	@ApiModelProperty(value = "本年佣金")
	private Long yearCommission = 0L;

	/**
	 * 累计分润
	 */
	@ApiModelProperty(value = "累计分润")
	private Long totalProfit = 0L;

}
