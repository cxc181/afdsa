package com.yuqian.itax.user.entity.po;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;

@Setter
@Getter
public class MemberProfitsRulesPO implements Serializable {
    private static final long serialVersionUID = -1L;

    /**
     * 主键id
     */
    private Long id;

    /**
     * 推广会费分润率
     */
    @NotNull(message="推广会费分润率不能为空")
    private BigDecimal membershipFee;
    /**
     *托管费分润率
     */
    @NotNull(message="托管费分润率不能为空")
    private BigDecimal profitsEntrustFeeRate;
    /**
     *下级是平级时托管费分润率
     */
    @NotNull(message="下级是平级时托管费分润率不能为空")
    private BigDecimal profitsPeersTwoEntrustFeeRate;
    /**
     *服务费分润率
     */
    @NotNull(message="服务费分润率不能为空")
    private BigDecimal serviceFeeRate;

    /**
     * 下级是平级时服务费分润率
     */
    @NotNull(message="下级是平级时服务费分润率不能为空")
    private BigDecimal profitsPeersTwoServiceFeeRate;
    /**
     * 下级是平级时会费分润率
     */
    @NotNull(message="下级是平级时会费分润率不能为空")
    private BigDecimal profitsPeersTwoMembershipFee;
    /**
     *消费折扣
     */
    @NotNull(message="消费折扣")
    private BigDecimal consumptionDiscount;

}
