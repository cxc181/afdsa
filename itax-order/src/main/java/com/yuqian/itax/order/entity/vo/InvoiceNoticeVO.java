package com.yuqian.itax.order.entity.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 *  @Author: Kaven
 *  @Date: 2020/5/20 17:15
 *  @Description: 开票订单通知接收VO
 */
@Getter
@Setter
public class InvoiceNoticeVO implements Serializable {
	
	private static final long serialVersionUID = -1L;
	
	/**
	 * 已通知订单总数
	 */
	@ApiModelProperty(value = "已通知订单总数")
	private Integer noticeCount;

	/**
	 * 未通知订单总数
	 */
	@ApiModelProperty(value = "未通知订单总数")
	private Integer unNoticeCount;

	/**
	 * 未处理订单数量总数
	 */
	@ApiModelProperty(value = "未处理订单数量总数")
	private Integer totalCount;

	/**
	 * 用户ID
	 */
	@ApiModelProperty(value = "用户ID")
	private Long userId;

	/**
	 * 机构编码
	 */
	@ApiModelProperty(value = "机构编码")
	private String oemCode;

}
