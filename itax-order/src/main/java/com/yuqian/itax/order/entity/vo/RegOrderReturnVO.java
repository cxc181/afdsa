package com.yuqian.itax.order.entity.vo;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

/**
 * @Description 企业注册订单返回bean-对外
 * @Author  Kaven
 * @Date   2020/7/17 11:36
*/
@Getter
@Setter
public class RegOrderReturnVO implements Serializable {
	
	private static final long serialVersionUID = -1L;

	/**
	 * 订单号
	 */
	private String orderNo;

    /**
     * 订单状态
     */
    private Integer orderStatus;

	/**
	 * 专属客服电话
	 */
	private String customerServicePhone;

	private Date addTime;//订单创建时间

}
