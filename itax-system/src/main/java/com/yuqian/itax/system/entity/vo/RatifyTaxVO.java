package com.yuqian.itax.system.entity.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.io.Serializable;

/**
 * @Description 核定税种VO
 * @Author  Kaven
 * @Date   2020/7/31 09:11
*/
@Getter
@Setter
public class RatifyTaxVO implements Serializable {
	
	private static final long serialVersionUID = -1L;
	
	/**
	 * 主键id
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@ApiModelProperty(value = "主键id")
	private Long id;

	/**
	 * 税种名称
	 */
	@ApiModelProperty(value = "税种名称")
	private String taxName;
}
