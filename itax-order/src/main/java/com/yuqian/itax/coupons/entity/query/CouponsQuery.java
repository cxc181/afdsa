package com.yuqian.itax.coupons.entity.query;

import com.yuqian.itax.common.base.entity.query.BaseQuery;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

/**
 * 用户优惠券查询实体
 *
 */
@Getter
@Setter
public class CouponsQuery extends BaseQuery {

	/**
	 * 可用范围 1-个体开户 2-个独开户 3-有限合伙开户 4-有限责任开户
	 */
	private String usableRange;

	/**
	 * 查询类型 1-可用优惠券 2-未使用优惠券 3-已失效优惠券
	 */
	private Integer type;
}
