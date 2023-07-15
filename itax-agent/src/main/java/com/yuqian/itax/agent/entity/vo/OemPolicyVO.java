package com.yuqian.itax.agent.entity.vo;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;

@Setter
@Getter
public class OemPolicyVO implements Serializable {
    private static final long serialVersionUID = -1L;

    /**
     * 机构名称
     */
    private String oemName;
    /**
     * 机构编码
     */
    private String oemCode;
    /**
     * 会费分润率
     */
    private BigDecimal membershipFee;

    /**
     * 开户分润率
     */
    private BigDecimal registerFee;

    /**
     * 开票分润率
     */
    private BigDecimal invoiceFee;

    /**
     * 企业注销服务费分润率
     */
    private BigDecimal cancelCompanyFee;

    /**
     * 结算周期
     */
    private Integer settlementCycle;

    /**
     * 结算类型 1-按周 2-按月
     */
    private Integer settlementType;
}
