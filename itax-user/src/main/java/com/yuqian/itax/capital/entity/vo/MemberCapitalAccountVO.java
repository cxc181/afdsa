package com.yuqian.itax.capital.entity.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.OptionalLong;

/**
 *  @Author: Kaven
 *  @Date: 2019/12/25 11:15
 *  @Description: 我的钱包金额展示bean
 */
@Getter
@Setter
public class MemberCapitalAccountVO implements Serializable {

    private static final long serialVersionUID = -1L;

    /**
     * 消费钱包可用余额
     */
    @ApiModelProperty(value = "消费钱包可用余额")
    private Long consumerUsableAmount;

    /**
     * 佣金钱包可用余额
     */
    @ApiModelProperty(value = "佣金钱包可用余额")
    private Long commissionUsableAmount;

    /**
     * 佣金钱包待结算金额
     */
    @ApiModelProperty(value = "佣金钱包待结算金额")
    private Long commissionOutstandingAmount;

    /**
     * 提现手续费（城市服务商）
     */
    @ApiModelProperty(value = "佣金钱包待结算金额")
    private BigDecimal diamondCommissionServiceFeeRate;

    /**
     * 提现手续费（非城市服务商）
     */
    @ApiModelProperty(value = "佣金钱包待结算金额")
    private BigDecimal commissionServiceFeeRate;

    /**
     * 消费钱包最小限额
     */
    private Long minConsumptionWalletLimit;

    /**
     * 佣金钱包最小限额
     */
    private Long minCommissionWalletLimit;

    /**
     * 总分润金额（佣金提现全选分润记录时用）
     */
    private Long allAmountProfitDetail = 0L;

    /**
     * 总分润记录笔数（佣金提现全选分润记录时用）
     */
    private Long quantityProfitDetail = 0L;

    /**
     * 最大分润记录id（佣金提现全选分润记录时用）
     */
    private Long maximalProfitDetailId;

    /**
     * 用户实名状态 0-未认证 1-认证成功 2-认证失败
     */
    private Integer authStatus;

    /**
     * 用户是否已绑定银行卡 0-否 1-是
     */
    private int isBindBankCard;

    /**
     * 用户提现方案 1-北京代付 2-纳呗 3-其他
     */
    private Integer withdrawSchemeSwitch;

    /**
     * 消费钱包可提现金额
     */
    private Long commissionUsableWithdrawAmount;
}
