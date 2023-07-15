package com.yuqian.itax.user.entity.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 *  @Author: lmh
 *  @Date: 2022/6/28
 *  @Description: 工商注册预订单
 */
@Getter
@Setter
public class RegPreOrderVO implements Serializable {
	
	private static final long serialVersionUID = -1L;

	/**
	 * 订单号
	 */
	private String orderNo;

	/**
	 * 注册资本（万元）
	 */
	private BigDecimal registeredCapital;

	/**
	 * 企业类型
	 */
	private Integer companyType;
}
