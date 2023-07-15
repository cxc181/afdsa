package com.yuqian.itax.order.entity.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

/**
 * 开票订单数量
 * 
 * @Date: 2020年03月05日 09:16:12
 * @author yejian
 */
@Getter
@Setter
public class InvoiceOrderCountVO implements Serializable {
	
	private static final long serialVersionUID = -1L;
	
	/**
	 * 审核中订单数量
	 */
	@ApiModelProperty(value = "审核中订单数量")
	private Long unCheckedCount;

	/**
	 * 待发货订单数量
	 */
	@ApiModelProperty(value = "待发货订单数量")
	private Long toBeShippedCount;

	/**
	 * 已发货订单数量
	 */
	@ApiModelProperty(value = "已发货订单数量")
	private Long toBeReceivedCount;

}
