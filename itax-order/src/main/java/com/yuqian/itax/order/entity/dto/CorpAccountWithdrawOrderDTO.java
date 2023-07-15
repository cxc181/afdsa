package com.yuqian.itax.order.entity.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @Description 对公户提现DTO
 * @Author  Kaven
 * @Date   2020/9/8 16:04
*/
@Getter
@Setter
public class CorpAccountWithdrawOrderDTO implements Serializable {
	
	private static final long serialVersionUID = -1L;

	// 当前登录人ID
	private Long currUserId;

	// 对公户ID
	@NotNull(message = "对公户ID不能为空")
	private Long corporateAccountId;

	// 对公户账号
	private String corporateAccount;
	// 制单员编号
	private String voucherMemberCode;

	// 收款记录IDS，多个用逗号隔开
	@NotEmpty(message = "收款记录ID不能为空")
	private String collectionRecordIds;

    // 开票订单号
    @NotEmpty(message = "开票订单号不能为空")
    private String invoiceOrderNo;

    // 机构编码
    private String oemCode;

    // 机构名称
    private String oemName;

    // 操作小程序来源 1-微信小程序 2-支付宝小程序
    private Integer sourceType;

    // 园区ID
    private Long parkId;

    /**
     * 提现金额
     */
    @NotNull(message = "提现金额不能为空")
    @ApiModelProperty(value = "提现金额")
    private Long amount;

    /**
     * 对公户配置id
     */
    private Long corporateAccountConfigId;
}
