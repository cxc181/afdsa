package com.yuqian.itax.user.entity.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

/**
 *  @Author: Kaven
 *  @Date: 2020/6/9 13:42
 *  @Description: 员工直推用户列表展示VO
 */
@Getter
@Setter
public class EmpManageOfPushListVO implements Serializable {
	
	private static final long serialVersionUID = -1L;

	/**
	 * 用户ID
	 */
	@ApiModelProperty(value = "用户ID")
	private Long userId;

	/**
	 * 用户姓名（优先显示实名，未实名显示昵称）
	 */
	@ApiModelProperty(value = "用户姓名（优先显示实名，未实名显示昵称）")
	private String memberName;

	/**
	 * 渠道用户id
	 */
	@ApiModelProperty(value = "渠道用户id")
	private Long channelUserId;

	/**
	 * 用户手机号
	 */
	@ApiModelProperty(value = "用户手机号")
	private String memberPhone;

	/**
	 * 机构编码
	 */
	@ApiModelProperty(value = "机构编码")
	private String oemCode;

	/**
	 * 等级标识 -1-员工 0-普通会员  1-VIP 2-白银会员 3-税务顾问 4-铂金会员 5-城市服务商
	 */
	@ApiModelProperty(value = "等级标识 -1-员工 0-普通会员 1-VIP 2-白银会员 3-税务顾问 4-铂金会员 5-城市服务商")
	private Integer levelNo;

	/**
	 * 等级名称
	 */
	@ApiModelProperty(value = "等级名称")
	private String levelName;

	/**
	 * 推广角色类型 1-散客 2-直客 3-顶级直客
	 */
	@ApiModelProperty(value = "推广角色类型 1-散客 2-直客 3-顶级直客")
	private Integer extendType;

	/**
	 * 备注
	 */
	@ApiModelProperty(value = "备注")
	private String remark;

	/**
	 * 直推用户数
	 */
	@ApiModelProperty(value = "直推用户数")
	private Long pushMemberCount = 0L;

	/**
	 * 个体数
	 */
	@ApiModelProperty(value = "个体数")
	private Long pushPersonalityCount = 0L;

	/**
	 * 本月开票额
	 */
	@ApiModelProperty(value = "本月开票")
	private Long monthInvoiceAmount = 0L;

	/**
	 * 本年开票额
	 */
	@ApiModelProperty(value = "本年开票")
	private Long yearInvoiceAmount = 0L;

	/**
	 * 累计开票额
	 */
	@ApiModelProperty(value = "累计开票")
	private Long totalInvoiceAmount = 0L;

	/**
	 * 最近一次开票时间
	 */
	@ApiModelProperty(value = "最近一次开票时间")
	private Date lastInvoiceTime;

	/**
	 * 最近一次开票金额
	 */
	@ApiModelProperty(value = "最近一次开票金额")
	private Long lastInvoiceAmount = 0L;

}
