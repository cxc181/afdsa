package com.yuqian.itax.user.entity.query;

import com.yuqian.itax.common.base.entity.query.BaseQuery;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class MemberLoginQuery extends BaseQuery {

	/**
	 * 会员账号
	 */
	private String useraccount;

	/**
	 * 密码
	 */
	private String password;

	/**
	 * 验证码
	 */
	private String vCode;
}
