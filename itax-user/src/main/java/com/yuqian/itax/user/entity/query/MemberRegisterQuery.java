package com.yuqian.itax.user.entity.query;

import com.yuqian.itax.common.base.entity.query.BaseQuery;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.io.Serializable;
import java.util.Date;

/**
 *  @Author: Kaven
 *  @Date: 2020/3/25 9:52
 *  @Description: 用户注册信息查询bean
 */
@Getter
@Setter
public class MemberRegisterQuery extends BaseQuery implements Serializable {

	/**
	 * OEM机构编号
	 */
	private String oemCode;

	/**
	 * 注册开始时间
	 */
//	@NotNull(message="注册开始时间不能为空")
//	@Pattern(regexp = "((19|20)[0-9]{2})-(0?[1-9]|1[012])-(0?[1-9]|[12][0-9]|3[01]) ([01]?[0-9]|2[0-3]):[0-5]?[0-9]:[0-5]?[0-9]", message = "注册开始时间格式不正确")
	private String registerStartTime;

	/**
	 * 注册结束时间
	 */
//	@NotNull(message="注册结束时间不能为空")
//	@Pattern(regexp = "((19|20)[0-9]{2})-(0?[1-9]|1[012])-(0?[1-9]|[12][0-9]|3[01]) ([01]?[0-9]|2[0-3]):[0-5]?[0-9]:[0-5]?[0-9]", message = "注册结束时间格式不正确")
	private String registerEndTime;

	/**
	 * 注册手机号
	 */
	private String regPhone;

	/**
	 * 注册身份证号
	 */
	private String idCard;

	/**
	 * 邀请码
	 */
	private String inviteCode;

}
