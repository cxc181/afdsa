package com.yuqian.itax.capital.entity.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * @Description 会员银行卡解绑接收dto
 * @Author  Kaven
 * @Date   2020/7/8 2:36 下午
*/
@Getter
@Setter
public class UnbindBankCardDTO implements Serializable {
	
	private static final long serialVersionUID = -1L;

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
}
