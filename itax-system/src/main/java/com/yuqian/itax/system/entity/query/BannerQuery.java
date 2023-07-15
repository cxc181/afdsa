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
 * banner图管理
 * 
 * @Date: 2019年12月08日 20:37:18 
 * @author 蒋匿
 */
@Getter
@Setter
public class BannerQuery extends BaseQuery {
	
	/**
	 * 标题（模糊匹配）
	 */
	private String likeTitle;

	/**
	 * 广告位置 1-首页 2-我的
	 */
	private Integer position;

	/**
	 * 添加时间（开始）
	 */
	private Date addTimeBeg;

	/**
	 * 添加时间（结束）
	 */
	private Date addTimeEnd;

	/**
	 * 添加人（模糊匹配）
	 */
	private String likeAddUser;

	/**
	 * 所属OEM（模糊匹配）
	 */
	private String likeOemName;

}
