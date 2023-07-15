package com.yuqian.itax.user.entity.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 已过期企业信息返回VO
 * @Date: 2020年07月17日 09:42
 * @author yejian
 */
@Getter
@Setter
public class OverdueCompanyInfoVO implements Serializable {
	
	private static final long serialVersionUID = -1L;

	/**
	 * 会员ID
	 */
	@ApiModelProperty(value = "会员ID ")
	private Long memberId;

	/**
	 * 会员账户
	 */
	@ApiModelProperty(value = "会员账户 ")
	private String memberAccount;

	/**
	 * 机构代码
	 */
	@ApiModelProperty(value = "机构代码")
	private String oemCode;

	/**
	 * 会员电话
	 */
	@ApiModelProperty(value = "会员电话")
	private String memberPhone;

	/**
	 * 企业ID
	 */
	@ApiModelProperty(value = "企业ID ")
	private String companyId;

	/**
	 * 是否符合发送通知条件
	 */
	@ApiModelProperty(value = "是否符合发送通知条件")
	private String isEligible;

	/**
	 * 是否已发送通知 0-未发送 1-已发送
	 */
	@ApiModelProperty(value = "是否发送通知")
	private String isSendNotice;

	/**
	 * 已过期企业数
	 */
	@ApiModelProperty(value = "已过期企业数")
	private Integer count;

	/**
	 * 到期时间剩余天数(仅即将过期通知需添加该字段数据)
	 */
	@ApiModelProperty(value = "到期时间剩余天数")
	private Integer surplusDays;
}