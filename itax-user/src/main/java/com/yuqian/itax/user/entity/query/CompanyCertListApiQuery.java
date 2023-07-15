package com.yuqian.itax.user.entity.query;

import com.yuqian.itax.common.base.entity.query.BaseQuery;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 *  @Author: yejian
 *  @Date: 2020/07/21 10:08
 *  @Description: 企业证件申请、归还查询参数Bean
 */
@Getter
@Setter
public class CompanyCertListApiQuery extends BaseQuery implements Serializable {
	
	private static final long serialVersionUID = -1L;

	/**
	 * 企业名称（模糊查询）
	 */
	@NotNull(message="企业名称不能为空")
	private String companyName;

	/**
	 * 会员账号（模糊查询）
	 */
	@NotNull(message="会员账号不能为空")
	private String regPhone;

	/**
	 * 订单号（模糊查询）
	 */
	@NotNull(message="订单号不能为空")
	private String orderNo;

}
