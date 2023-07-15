package com.yuqian.itax.system.entity.query;

import com.yuqian.itax.common.base.entity.query.BaseQuery;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

/**
 * 意见反馈
 * 
 * @Date: 2019年12月08日 20:38:54 
 * @author 蒋匿
 */
@Setter
@Getter
public class FeedbackQuery extends BaseQuery {

	/**
	 * 用户姓名(模糊)（优先显示实名姓名，没有则显示昵称）
	 */
	private String likeUserName;

	/**
	 * 用户手机号(模糊)
	 */
	private String likeMemberPhone;

	/**
	 * 反馈时间（开始）
	 */
	private Date addTimeBeg;

	/**
	 * 反馈时间（结束）
	 */
	private Date addTimeEnd;

	/**
	 * 意见内容（模糊）
	 */
	private String likeFeedbackContent;

	/**
	 * 所属OEM机构（模糊）
	 */
	private String likeOemName;
	
}
