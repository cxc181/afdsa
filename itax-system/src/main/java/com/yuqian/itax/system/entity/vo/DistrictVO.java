package com.yuqian.itax.system.entity.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.io.Serializable;

/**
 *  @Author: yejian
 *  @Date: 2019/12/23 17:14
 *  @Description: 区
 */
@Getter
@Setter
public class DistrictVO implements Serializable {
	
	private static final long serialVersionUID = -1L;
	
	/**
	 * 主键id
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@ApiModelProperty(value = "主键id")
	private Integer id;

	/**
	 * 市编码
	 */
	@ApiModelProperty(value = "市编码")
	private String cityCode;

	/**
	 * 区编码
	 */
	@ApiModelProperty(value = "区编码")
	private String code;

	/**
	 * 区名称
	 */
	@ApiModelProperty(value = "区名称")
	private String name;

}
