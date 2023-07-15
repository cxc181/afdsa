package com.yuqian.itax.tax.entity.query;

import com.yuqian.itax.common.base.entity.query.BaseQuery;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ParkTaxBillFileRecordQuery  extends BaseQuery {
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
     * 企业id
     */
    private Long parkTaxBill;
}
