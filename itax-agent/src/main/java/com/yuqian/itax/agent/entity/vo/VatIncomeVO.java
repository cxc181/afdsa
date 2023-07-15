package com.yuqian.itax.agent.entity.vo;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 *  @Author: Kaven
 *  @Date: 2020/3/27 10:37
 *  @Description: 增值税所得税返回bean
 */
@Setter
@Getter
public class VatIncomeVO implements Serializable {

    private static final long serialVersionUID = -1L;

    /**
     * 增值税所得税总计
     */
    private BigDecimal totalTax;

    /**
     *  增值税所得税明细列表
     */
    private List<VatIncomeDetailVO> vatIncomeDetailList;
}
