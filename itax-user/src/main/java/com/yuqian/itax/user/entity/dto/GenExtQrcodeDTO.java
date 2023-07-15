package com.yuqian.itax.user.entity.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.io.Serializable;

/**
 *  @Author: Kaven
 *  @Date: 2020/3/26 11:01
 *  @Description: 小程序推广二维码接收参数DTO
 */
@Getter
@Setter
public class GenExtQrcodeDTO implements Serializable {

	private static final long serialVersionUID = -1L;

	/**
	 * OEM机构编号
	 */
	private String oemCode;

	/**
	 * 操作小程序来源 1-微信小程序 2-支付宝小程序
	 */
	private String sourceType;

	/**
	 * 国金助手编码
	 */
	private String channelCode;

	/**
	 * 邀请码：支持6到20位数字或字母组合
	 */
	@NotBlank(message = "邀请码不能为空：6到20位数字或字母组合")
	@Pattern(regexp = "^[a-zA-Z0-9]{6,20}$", message = "邀请码格式不正确：必须为6到20位数字或字母的组合")
	private String inviteCode;
}
