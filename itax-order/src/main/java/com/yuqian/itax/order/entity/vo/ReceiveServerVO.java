package com.yuqian.itax.order.entity.vo;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 *  @Author: Kaven
 *  @Date: 2020/5/19 09:31
 *  @Description: 接单客服返回VO
 */
@Getter
@Setter
public class ReceiveServerVO implements Serializable {
	
	private static final long serialVersionUID = -1L;
	
	/**
	 * 接单客服ID
	 */
	private Long recvOrderUserId;
	
	/**
	 * 工单号
	 */
	private String workOrderNo;
}
