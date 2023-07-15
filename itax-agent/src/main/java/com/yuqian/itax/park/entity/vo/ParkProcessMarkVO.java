package com.yuqian.itax.park.entity.vo;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
public class ParkProcessMarkVO implements Serializable {

    private static final long serialVersionUID = -1L;
    /**
     * 园区id
     */
    private Long id;

    /**
     * 园区编码
     */
    private String parkCode;

    /**
     * 流程标记
     */
    private Integer processMark;

    /**
     * 支付金额
     */
    private Long paymentAmount;

    /**
     * 产品id
     */
    private Long productId;

    /**
     * 客服电话
     */
    private String mobile;
}
