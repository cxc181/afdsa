package com.yuqian.itax.park.entity.vo;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 *  @Author: Kaven
 *  @Date: 2019/12/10 11:21
 *  @Description: 税费测算工具园区列表
 */
@Getter
@Setter
public class TaxCalculatorParkVO implements Serializable {
	
	private static final long serialVersionUID = -1L;
	
	/**
	 * 园区id
	 */
	private Long parkId;
	
	/**
	 * 园区名称
	 */
	private String parkName;

	/**
	 * 产品id
	 */
	private Long productId;
}
