package com.yuqian.itax.user.entity.vo;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;


/**
 *  @Author: liumenghao
 *  @Date: 2021/02/09
 *  @Description: 企业过期状态返回VO
 */
@Getter
@Setter
public class CompanyOverdueStatusReminderVO implements Serializable {

	private static final long serialVersionUID = -1L;

	/**
	 * 用户ID
	 */
	private Long memberId;

	/**
	 * 企业ID
	 */
	private Long id;

	/**
	 * 是否触发提醒通知条件 0-不触发 1-触发
	 */
	private Integer isTrigger;
}
