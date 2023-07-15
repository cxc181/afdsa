package com.yuqian.itax.order.entity.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

/**
 * 企业开票返回VO
 * @Date: 2020年07月17日 09:16:12
 * @author yejian
 */
@Getter
@Setter
public class CompanyInvoiceApiVO implements Serializable {
	
	private static final long serialVersionUID = -1L;

	/**
	 * 订单号
	 */
	@ApiModelProperty(value = "订单号")
	private String orderNo;

	/**
	 * 订单状态 0-待创建 1-待付款 2-待审核 3-出票中 4-待发货 5-出库中 6-待收货 7-已签收 8-已取消 9-待出款
	 */
	@ApiModelProperty(value = "订单状态 0-待创建 1-待付款 2-待审核 3-出票中 4-待发货 5-出库中 6-待收货 7-已签收 8-已取消 9-待出款")
	private Integer orderStatus;

	/**
	 * 创建时间
	 */
	@ApiModelProperty(value = "创建时间")
	private Date addTime;

}
