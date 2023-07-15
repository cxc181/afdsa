package com.yuqian.itax.user.entity.query;

import com.yuqian.itax.common.base.entity.query.BaseQuery;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

/**
 * 企业管理查询条件
 * @author：pengwei
 * @Date：2020/01/08 15:12
 * @version：1.0
 */
@Getter
@Setter
public class MemberCompanyQuery extends BaseQuery implements Serializable {

	private static final long serialVersionUID = -1L;
	/**
	 * 机构编码
	 */
	private String oemCode;
	/**
	 * 会员手机号（模糊查询）
	 */
	private String likeMemberPhone;
	/**
	 * 用户名
	 */
	private String realName;
	/**
	 * 企业名称（模糊查询）
	 */
	private String likeCompName;
	/**
	 * 企业类型 1-个体户 2-个人独资企业 3-有限合伙公司 4-有限责任公司
	 */
	private Integer companyType;
	/**
	 * 园区id
	 */
	private Long parkId;
	/**
	 * 添加时间开始
	 */
	private Date addTimeBeg;
	/**
	 * 添加时间结束
	 */
	private Date addTimeEnd;
	/**
	 * 机构名称（模糊）
	 */
	private String likeOemName;
	/**
	 * 年费状态：1未过期，2-即将过期 3已过期
	 */
	private Integer overdue;
	/**
	 * 企业状态 1-正常 2-已冻结 3-已注销
	 */
	private Integer companyStatus;
	/**
	 * 经营者名称
	 */
	private String operatorName;
	/**
	 * 托管状态
	 */
	private Integer hostingStatus;

	/**
	 * 托管状态
	 */
	private Long companyId;
	/**
	 * 税号
	 */
	private String ein;

	/**
	 * 税务登记日期开始
	 */
	private String startTaxRegDate;
	/**
	 * 税务登记日期结束
	 */
	private String endTaxRegDate;

	/**
	 * 纳税人类型  1-小规模纳税人 2-一般纳税人
	 */
	private Integer taxpayerType;
}