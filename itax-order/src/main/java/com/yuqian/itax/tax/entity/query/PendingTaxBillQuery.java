package com.yuqian.itax.tax.entity.query;

import com.yuqian.itax.common.base.entity.query.BaseQuery;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * 校验待处理企业税单Query
 */
@Setter
@Getter
public class PendingTaxBillQuery extends BaseQuery {
    private static final long serialVersionUID = -1L;

    /**
     * 税款所属期年
     */
    private Integer taxBillYear;

    /**
     * 税款所属期
     */
    private Integer taxBillSeasonal;

    /**
     * 企业ID
     */
    private Long companyId;

    /**
     * 企业税号
     */
    private String ein;

    /**
     * 税期范围 1-当前税期 2-历史税期 （属性值为空或不为1、2时查询所有税期）
     */
    private Integer range;

    /**
     * 税单状态范围 1-未确认成本(含待核对状态) 2-未申报完成 3-不含待核对状态  4 申报完成 5-申报中
     */
    private Integer statusRange;

    /**
     * 所得税征收方式
     */
    private Integer incomeLevyType;
}
