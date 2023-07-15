package com.yuqian.itax.user.entity.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @Description 对公户申请详情VO
 * @Author yejian
 * @Date 2020/09/08 16:13
 */
@Getter
@Setter
public class CompanyCorpAccApplyDetailVO implements Serializable {

	private static final long serialVersionUID = -1L;

	/**
	 * 园区名称
	 */
	@ApiModelProperty(value = "园区名称")
	private String parkName;

	/**
	 * 账户托管费
	 */
	@ApiModelProperty(value = "账户托管费")
	private Long prodAmount = 0L;

	/**
	 * 办理费
	 */
	@ApiModelProperty(value = "办理费")
	private Long processingFee = 0L;

	/**
	 * 银行开户费
	 */
	@ApiModelProperty(value = "银行开户费")
	private String bankActivationFee;

	/**
	 * 银行其他费用
	 */
	@ApiModelProperty(value = "银行其他费用")
	private String bankOtherFee;

	/**
	 * 银行提现手续费
	 */
	@ApiModelProperty(value = "银行提现手续费")
	private String bankWithdrawalFee;

	/**
	 * 提现限额单笔
	 */
	@ApiModelProperty(value = "提现限额单笔")
	private Long singleWithdrawalLimit;

	/**
	 * 提现限额单日
	 */
	@ApiModelProperty(value = "提现限额单日")
	private Long dailyWithdrawalLimit;
}
