package com.yuqian.itax.system.entity.query;

import com.yuqian.itax.common.base.entity.query.BaseQuery;
import lombok.Getter;
import lombok.Setter;

/**
 * 行业
 *
 * @Date: 2019年12月08日 20:37:33
 * @author 蒋匿
 */
@Getter
@Setter
public class IndustryQuery extends BaseQuery {
	
	/**
	 * 园区id
	 */
	private Long parkId;

	/**
	 * 服务类别（模糊匹配）
	 */
	private String likeIndustryName;

	/**
	 * 经营范围（模糊匹配）
	 */
	private String likeBusinessContent;

	/**
	 * 核定税种（模糊匹配）
	 */
	private String likeTaxName;

	/**
	 * 开票类目（模糊匹配）
	 */
	private String likeCategoryName;

	/**
	 * 行业主键
	 */
	private Long industryId;

	/**
	 * 企业类型 1-个体工商户 2-个人独资企业 3-有限合伙 4-有限责任
	 */
	private Integer companyType;
}
