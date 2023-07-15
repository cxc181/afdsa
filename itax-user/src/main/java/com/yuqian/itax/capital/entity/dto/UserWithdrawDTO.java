package com.yuqian.itax.capital.entity.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 *  @Author: Kaven
 *  @Date: 2019/12/16 11:17
 *  @Description: 用户提现dto
 */
@Getter
@Setter
public class UserWithdrawDTO implements Serializable {
	
	private static final long serialVersionUID = -1L;

	// 当前登录人ID
	private Long userId;
	// 服务商/员工id
	private Long agentId;

	// 开票主体企业ID，佣金钱包提现时必传
	private Long companyId;

	// 机构编码
	private String oemCode;

	/**
	 * 验证码
	 */
	@NotBlank(message = "验证码不能为空")
	@ApiModelProperty(value = "验证码")
	private String verifyCode;

	/**
	 * 手机号码
	 */
	@ApiModelProperty(value = "手机号码（注册手机号）")
	private String mobile;
	
	/**
	 * 提现卡号
	 */
	@NotBlank(message = "银行卡号不能为空")
	@ApiModelProperty(value = "银行卡号")
	private String bankNumber;

	/**
	 * 提现银行
	 */
	@ApiModelProperty(value = "提现银行")
	private String bankName;
	/**
	 * 开票类型  1-增值税普通发票 2-增值税专用发票
	 */
	private Integer invoiceType;
	/**
	 * 开票类型  1-增值税普通发票 2-增值税专用发票
	 */
	private String invoiceName;
	/**
	 * 开票类目名称，城市服务商佣金提现时必传
	 */
    private String categoryName;

    /**
     * 提现金额
     */
    @NotNull(message = "提现金额不能为空")
    @ApiModelProperty(value = "提现金额")
    private Long amount;

    /**
     * 银行卡类型 1-储蓄卡 2-信用卡
     */
    private Integer bankCardType;

    /**
     * 提现类型 1-消费钱包提现 2-佣金钱包提现
     */
    private Integer withdrawType;

	private Integer sourceType;// 操作小程序来源 1-微信小程序 2-支付宝小程序
	/**
	 * 抬头公司名称
	 */
	private String companyName;
	/**
	 * 抬头公司地址
	 */
	private String companyAddress;
	/**
	 * 抬头税号
	 */
	private String ein;
	/**
	 * 抬头电话
	 */
	private String phone;
	/**
	 * 发票抬头收件人
	 */
	private String recipient;
	/**
	 * 发票抬头联系电话
	 */
	private String recipientPhone;

	/**
	 * 发票抬头详细地址
	 */
	private String recipientAddress;
	/**
	 * 发票抬头省编码
	 */
	private String provinceCode;

	/**
	 * 发票抬头省名称
	 */
	private String provinceName;

	/**
	 * 发票抬头市编码
	 */
	private String cityCode;

	/**
	 * 发票抬头市名称
	 */
	private String cityName;

	/**
	 * 发票抬头区编码
	 */
	private String districtCode;

	/**
	 * 发票抬头区名称
	 */
	private String districtName;

	/**
	 * 邮箱
	 */
	private String email;
	/**
	 * 佣金提现增值税税率
	 */
	private BigDecimal vatRate;

	/**
	 * 是否全选分润记录 0-否 1-是（佣金提现全选分润记录时用）
	 */
	private Integer isCheckAllProfitDetail;

	/**
	 * 最大分润记录id（佣金提现全选分润记录时用）
	 */
	private Long maximalProfitDetailId;

	/**
	 * 分润记录id（多个分润记录使用英文逗号分隔）
	 */
	private String profitDetailIds;
}
