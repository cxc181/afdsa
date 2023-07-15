package com.yuqian.itax.order.entity.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 计算返回服务费VO
 * @author：pengwei
 * @Date：2020/12/7 15:01
 * @version：1.0
 */
@Getter
@Setter
public class InvoiceServiceFeeVO implements Serializable {

	private static final long serialVersionUID = -1L;

	/**
	 * 本次开票服务费费率
	 */
	private BigDecimal serviceRate;

	/**
	 * 是否有下一阶段费率（0：没有，1：有）
	 */
	private Integer hasNext = 0;

	/**
	 * 再开票金额
	 */
	private Long amount;

	/**
	 * 下一阶段服务费费率
	 */
	private BigDecimal nextServiceRate;

}
