package com.yuqian.itax.coupons.entity;

import java.io.Serializable;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;
/**
 * 优惠券兑换码表
 * 
 * @Date: 2021年06月04日 09:33:54 
 * @author 蒋匿
 */
@Getter
@Setter
@Table(name="t_e_coupon_exchange_code")
public class CouponExchangeCodeEntity implements Serializable {
	
	private static final long serialVersionUID = -1L;
	
	/**
	 * 主键id
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	/**
	 * 兑换码
	 */
	private String exchangeCode;
	
	/**
	 * 兑换码名称
	 */
	private String exchangeName;
	
	/**
	 * 限量兑换张数
	 */
	private Integer limitedNumber;
	
	/**
	 * 每人可兑换张数
	 */
	private Integer exchangeNumberPerson;
	
	/**
	 * 已兑换张数
	 */
	private Integer hasExchangeNumber;
	
	/**
	 * 状态 0-未生效 1-已生效 2-已过期 3-已作废 4-已暂停
	 */
	private Integer status;
	
	/**
	 * 优惠券id
	 */
	private Long couponsId;
	
	/**
	 * 生效日期
	 */
	private Date startDate;
	
	/**
	 * 截至日期
	 */
	private Date endDate;
	
	/**
	 * 创建日期
	 */
	private Date addTime;
	
	/**
	 * 创建人
	 */
	private String addUser;
	
	/**
	 * 更新时间
	 */
	private Date updateTime;
	
	/**
	 * 修改人
	 */
	private String updateUser;
	
	/**
	 * 备注
	 */
	private String remark;
	
	
}
