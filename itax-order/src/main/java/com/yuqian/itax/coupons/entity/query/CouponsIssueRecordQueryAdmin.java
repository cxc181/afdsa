package com.yuqian.itax.coupons.entity.query;

import com.yuqian.itax.common.base.entity.query.BaseQuery;
import lombok.Getter;
import lombok.Setter;
import org.jeecgframework.poi.excel.annotation.Excel;

import javax.validation.constraints.NotBlank;
import java.util.Date;

/**
 * 用户优惠券查询实体
 *
 */
@Getter
@Setter
public class CouponsIssueRecordQueryAdmin extends BaseQuery {

	/**
	 * 优惠券发放记录id
	 */
	private Long id;

	/**
	 * 账号
	 */
	private String memberAccount;

	/**
	 * 名称
	 */
	private String realName;
	/**
	 * OEM机构
	 */
	private String oemCode;
	/**
	 * 优惠券名称
	 */
	private String couponsName;

	/**
	 * 优惠券编码
	 */
	private String couponsCode;

	/**
	 * 优惠券使用状态 状态  0-未使用 1-已使用 2-已过期 3-已撤回
	 */
	private Integer status;
	/**
	 *发放方式 0-批量发放 1-兑换码
	 */
	private Integer issueType;
}
