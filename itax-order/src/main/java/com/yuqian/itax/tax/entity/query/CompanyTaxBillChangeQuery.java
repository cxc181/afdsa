package com.yuqian.itax.tax.entity.query;

import com.yuqian.itax.common.base.entity.query.BaseQuery;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CompanyTaxBillChangeQuery extends BaseQuery {

    /**
     * 企业税单id
     */
    private Long companyTaxBillId;
}
