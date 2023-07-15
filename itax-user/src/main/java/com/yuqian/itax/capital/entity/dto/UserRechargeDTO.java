package com.yuqian.itax.capital.entity.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 *  @Author: Kaven
 *  @Date: 2019/12/16 11:17
 *  @Description: 用户充值dto
 */
@Getter
@Setter
public class UserRechargeDTO implements Serializable {
	
	private static final long serialVersionUID = -1L;

	private Long currUserId;// 当前登录人ID

	private String oemCode;// OEM机构编码

	/**
	 * 充值金额
	 */
	@NotNull(message = "充值金额不能为空")
	@ApiModelProperty(value = "充值金额")
	private Long amount;

	/**
	 * 支付方式 1-微信 2-余额 3-支付宝 4-易宝支付 5-建行 6-北京代付 7-纳呗
	 */
	@NotNull(message = "支付方式不能为空")
	@ApiModelProperty(value = "支付方式")
	private Integer payType = 1;

	private Integer sourceType;// 操作小程序来源 1-微信小程序 2-支付宝小程序

	private String buyerId;// 买家支付宝用户号

	private String buyerLogonId;// 买家支付宝账号

	private String memberAccount;// 会员账号

	/**
	 * 待收单机构编码
	 */
	private String otherPayOemCode;
}
