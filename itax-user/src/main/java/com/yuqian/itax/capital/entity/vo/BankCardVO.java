package com.yuqian.itax.capital.entity.vo;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 *  @Author: Kaven
 *  @Date: 2019/12/16 11:17
 *  @Description: 会员银行卡展示类
 */
@Getter
@Setter
public class BankCardVO implements Serializable {
	
	private static final long serialVersionUID = -1L;
	
	/**
	 * 主键id
	 */
	private Long id;

	/**
	 * 开户行编码
	 */
	private String bankCode;
	
	/**
	 * 开户行
	 */
	private String bankName;

	/**
	 * 预留手机号
	 */
	private String reserveMobile;

	/**
	 * 银行logo
	 */
	private String logo;

	/**
	 * 银行卡号
	 */
	private String bankNumber;

	/**
	 * 银行卡类型 1-储蓄卡 2-信用卡
	 */
	private Integer bankCardType;

	/**
	 * 储蓄卡单笔限额
	 */
	private Integer singleLimitCash;

	/**
	 * 储蓄卡单日限额
	 */
	private Integer dailyLimitCash;

	/**
	 * 储蓄卡单月限额
	 */
	private Integer monthLimitCash;
}
