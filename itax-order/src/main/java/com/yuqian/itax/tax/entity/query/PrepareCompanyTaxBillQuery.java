package com.yuqian.itax.tax.entity.query;

import com.yuqian.itax.common.base.entity.query.BaseQuery;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class PrepareCompanyTaxBillQuery extends BaseQuery {
    private static final long serialVersionUID = -1L;

    /**
     * 会员id
     */
    private Long memberId;

    /**
     * 企业ID
     */
    private Long companyId;

}
