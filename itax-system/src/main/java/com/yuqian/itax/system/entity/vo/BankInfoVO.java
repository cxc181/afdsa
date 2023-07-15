package com.yuqian.itax.system.entity.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 银行信息
 * 
 * @Date: 2019年12月19日 11:35:19
 * @author yejian
 */
@Getter
@Setter
public class BankInfoVO implements Serializable {
	
	private static final long serialVersionUID = -1L;

	/**
	 * 银行名称
	 */
	@ApiModelProperty(value = "银行名称")
	private String bankName;
	
	/**
	 * logo图片地址
	 */
	@ApiModelProperty(value = "logo图片地址")
	private String bankLogoUrl;

	/**
	 * 储蓄卡单笔限额
	 */
	@ApiModelProperty(value = "储蓄卡单笔限额")
	private Integer singleLimitCash;

	/**
	 * 储蓄卡单日限额
	 */
	@ApiModelProperty(value = "储蓄卡单日限额")
	private Integer dailyLimitCash;
	
}
