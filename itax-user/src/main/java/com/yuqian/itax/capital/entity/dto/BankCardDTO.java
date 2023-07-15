package com.yuqian.itax.capital.entity.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 *  @Author: Kaven
 *  @Date: 2019/12/16 11:17
 *  @Description: 用户绑卡接收BEAN
 */
@Getter
@Setter
public class BankCardDTO implements Serializable {
	
	private static final long serialVersionUID = -1L;
	/**
	 *  当前用户ID
	 */
	private Long userId;

	/**
	 *  机构编码
	 */
	private String oemCode;
	
	/**
	 * 主键id
	 */
	@ApiModelProperty(value = "主键ID")
	private Long id;

	/**
	 * 验证码
	 */
	@NotBlank(message = "验证码不能为空")
	@ApiModelProperty(value = "验证码")
	private String verifyCode;
	
	/**
	 * 开户行
	 */
	private String bankName;

	/**
	 * 银行卡号
	 */
	@ApiModelProperty(value = "银行卡号")
	private String bankNumber;

	/**
	 * 银行卡类型 1-储蓄卡 2-信用卡
	 */
	private Integer bankCardType;

	/**
	 * 用户姓名
	 */
	@ApiModelProperty(value = "用户姓名")
	private String name;

	/**
	 * 身份证号码
	 */
	@ApiModelProperty(value = "身份证号码")
	private String idCardNo;

	/**
	 * 预留手机号
	 */
	@ApiModelProperty(value = "预留手机号")
	private String reserveMobile;

	/**
	 *  身份证正面照
	 */
	@NotBlank(message = "身份证正面照不能为空")
	private String idCardFront;

	/**
	 *  身份证反面照
	 */
	@NotBlank(message = "身份证反面照不能为空")
	private String idCardBack;

	/**
	 *  身份证有效期
	 */
	@NotBlank(message = "身份证有效期不能为空")
	private String expireDate;

	/**
	 *  身份证地址
	 */
	@NotBlank(message = "身份证地址不能为空")
	private String idCardAddr;

	private String registRedisTime;// redis标识值
}
