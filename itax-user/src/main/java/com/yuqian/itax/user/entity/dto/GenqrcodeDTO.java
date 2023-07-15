package com.yuqian.itax.user.entity.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 生成小程序二维码参数
 * 
 * @Date: 2019年12月07日 20:48:40 
 * @author yejian
 */
@Getter
@Setter
public class GenqrcodeDTO implements Serializable {
	
	private static final long serialVersionUID = -1L;

	/**
	 * 场景
	 */
	@NotBlank(message="场景不能为空")
	@ApiModelProperty(value = "场景")
	private String scene;

	/**
	 * 图片大小
	 */
	@NotNull(message="图片大小不能为空")
	@ApiModelProperty(value = "图片大小")
	private Long width;
	/**
	 * 二维码中跳向的地址
	 */
	@ApiModelProperty(value = "二维码中跳向的地址")
	private String page;

	/**
	 *用户来源 1-国金助手 2-云财体系
	 */
	private Integer userSourceType;
}
