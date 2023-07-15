package com.yuqian.itax.system.entity.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.io.Serializable;

/**
 *  @Author: Kaven
 *  @Date: 2019/12/9 17:14
 *  @Description: 省份
 */
@Getter
@Setter
public class SysProvinceVO implements Serializable {
	
	private static final long serialVersionUID = -1L;
	
	/**
	 * 主键id
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@ApiModelProperty(value = "主键id")
	private Long id;

	/**
	 * 省编码
	 */
	@ApiModelProperty(value = "省编码")
	private String code;

	/**
	 * 省名称
	 */
	@ApiModelProperty(value = "省名称")
	private String name;
	
}
