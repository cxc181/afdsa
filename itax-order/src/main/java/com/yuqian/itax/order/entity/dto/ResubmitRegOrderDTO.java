package com.yuqian.itax.order.entity.dto;

import lombok.Getter;
import lombok.Setter;
import org.omg.CORBA.PRIVATE_MEMBER;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.io.Serializable;

/**
 *  @Author: Kaven
 *  @Date: 2020/6/3 14:40
 *  @Description: 企业注册订单重新提交DTO
 */
@Getter
@Setter
public class ResubmitRegOrderDTO implements Serializable {
	
	private static final long serialVersionUID = -1L;

	private String regPhone;// 经营者手机号（外部调用时）

	/**
	 * 订单号
	 */
	@NotBlank(message = "订单号不能为空")
	private String orderNo;

	/**
	 * 机构编码
	 */
	private String oemCode;
	
	/**
	 * 字号
	 */
	private String shopName;

	/**
	 * 备选字号1
	 */
	private String shopNameOne;

	/**
	 * 备选字号2
	 */
	private String shopNameTwo;

	private Integer orderStatus; // 订单状态

	/**
	 * 身份证正面照地址
	 */
	private String idCardFront;

	/**
	 * 身份证反面照地址
	 */
	private String idCardReverse;

	/**
	 * 视频地址
	 */
	private String videoAddr;
}
