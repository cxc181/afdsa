package com.yuqian.itax.order.entity.vo;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

/**
 *  @Author: Kaven
 *  @Date: 2020/3/25 14:21
 *  @Description: 企业注册订单返回bean-拓展宝
 */
@Getter
@Setter
public class RegOrderVO implements Serializable {
	
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
	 * 注册手机号
	 */
	private String regPhone;
	
	/**
	 * 用户真实姓名
	 */
	private String realName;
	
	/**
	 * 经营者姓名
	 */
	private String operatorName;

	/**
	 * 会员等级
	 */
	private Integer levelNo;

	/**
	 * 用户邀请码
	 */
	private String inviteCode;

	/**
	 * 企业名称
	 */
	private String registerName;

	/**
	 * 企业ID
	 */
	private Long companyId;
	
	/**
	 * 企业类型
	 */
	private Integer companyType;

	/**
	 * 企业注册年费
	 */
	private Long orderAmount;

	/**
	 * 优惠金额
	 */
	private Long discountAmount;

	/**
	 * 支付金额
	 */
	private Long payAmount;

	/**
	 * 园区
	 */
	private String parkName;

	/**
	 * 创建时间
	 */
	private Date createTime;

	/**
	 * 是否已开启身份验证 0-未开启 1-已开启
	 */
	private Integer isOpenAuthentication;

}
