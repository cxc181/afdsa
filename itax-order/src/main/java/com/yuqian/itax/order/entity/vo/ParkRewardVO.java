package com.yuqian.itax.order.entity.vo;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @Description 园区奖励VO
 * @Author  lmh
 * @Date   2022/09/26
 */
@Getter
@Setter
public class ParkRewardVO implements Serializable {

    private static final long serialVersionUID = -1L;

    /**
     * 园区id
     */
    private Long parkId;

    /**
     * 园区名称
     */
    private String parkName;

    /**
     * 增值附加税奖励金额（分）
     */
    private Long vatAndSurchargeAward;

    /**
     * 所得税奖励金额（分）
     */
    private Long incomeTaxAward;

    /**
     * 合计奖励金额（分）
     */
    private Long allAward;
}
