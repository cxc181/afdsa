package com.yuqian.itax.order.entity.vo;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 *  @Author: Kaven
 *  @Date: 2019/12/24 16:05
 *  @Description: 接单表查询接收bean
 */
@Getter
@Setter
public class ReceiveOrderVO implements Serializable {
	
	private static final long serialVersionUID = -1L;
	
	/**
	 * 主键id
	 */
	private Long id;
	
	/**
	 * 客服id，系统用户id
	 */
	private Long userId;
	
	/**
	 * 最小接单数量
	 */
	private Integer minNum;

	/**
	 * 最小接单数量相同的客服数
	 */
	private Integer minCustomerNum;

	/**
	 * 总接单数量
	 */
	private Integer totalRecv;

	/**
	 * 总坐席客服数
	 */
	private Integer customerNum;
}
