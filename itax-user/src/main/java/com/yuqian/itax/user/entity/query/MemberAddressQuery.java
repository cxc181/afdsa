package com.yuqian.itax.user.entity.query;

import com.yuqian.itax.common.base.entity.query.BaseQuery;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 会员收件地址查询
 * @author：pengwei
 * @Date：2020/12/25 16:57
 * @version：1.0
 */
@Getter
@Setter
public class MemberAddressQuery extends BaseQuery {

	/**
	 * 会员id
	 */
	private Long memberId;

	/**
	 * 机构编码
	 */
	private String oemCode;

	/**
	 * 状态  1-可用 0-不可用
	 */
	private Integer status;
}