package com.yuqian.itax.order.entity.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 开票补传流水订单数量
 * 
 * @Date: 2020年05月19日 09:16:12
 * @author yejian
 */
@Getter
@Setter
public class InvoiceWaterCountVO implements Serializable {
	
	private static final long serialVersionUID = -1L;
	
	/**
	 * 补传流水总数量
	 */
	@ApiModelProperty(value = "补传流水总数量")
	private int waterOrderCount;

	/**
	 * 补传流水待补传订单数量
	 */
	@ApiModelProperty(value = "补传流水待补传订单数量")
	private int waterToBePatchCount;

	/**
	 * 补传流水审核中订单数量
	 */
	@ApiModelProperty(value = "补传流水审核中订单数量")
	private int waterToBeAuditCount;

	/**
	 * 交易成果总数量
	 */
	@ApiModelProperty(value = "交易成果总数量")
	private int achievementOrderCount;

	/**
	 * 交易成果待补传订单数量
	 */
	@ApiModelProperty(value = "交易成果待补传订单数量")
	private int achievementToBePatchCount;

	/**
	 * 交易成果审核中订单数量
	 */
	@ApiModelProperty(value = "交易成果审核中订单数量")
	private int achievementToBeAuditCount;

}
