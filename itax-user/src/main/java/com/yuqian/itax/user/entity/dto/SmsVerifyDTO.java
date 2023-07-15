package com.yuqian.itax.user.entity.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 *  @Author: Kaven
 *  @Date: 2019/12/19 15:02
 *  @Description: 短信验证码接收DTO
 */
@Getter
@Setter
public class SmsVerifyDTO implements Serializable {
	
	private static final long serialVersionUID = -1L;

	/**
	 * 会员账号（手机号）
	 */
	@NotBlank(message="手机号不能为空")
	@ApiModelProperty(value = "会员账号（手机号）")
	private String phone;

	/**
	 * 验证码类型
	 */
	@NotBlank(message="验证码类型不能为空")
	@ApiModelProperty(value = "验证码类型：1->注册；2->开户；3->开票；4->资金变动；5->通知；6->用户绑卡；7->用户解绑；8->用户提现；9->用户开票订单余额支付;29->对公户提现")
	private String type;
}
