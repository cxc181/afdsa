package com.yuqian.itax.tax.entity.query;

import com.yuqian.itax.common.base.entity.query.BaseQuery;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Setter
@Getter
public class CompanyTaxBillQuery extends BaseQuery {
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
     * 会员id
     */
    private Long memberId;

    /**
     * 企业ID
     */
    private Long companyId;

    /**
     * 税单类型 1-全部，2-待处理
     */
    @NotNull(message="税单类型不能为空")
    @Min(value = 1, message = "税单类型有误")
    @Max(value = 2, message = "税单类型有误")
    private Integer taxBillType;

    /**
     * 税单状态 0-待确认 1-待退税 2-待补税 3-正常 4-已退税 5-已补税
     */
    private Integer taxBillStatus;

    /**
     * 超时时间
     */
    private Integer overTime;

    /**
     * 园区ID
     */
    private Long parkId;
}
