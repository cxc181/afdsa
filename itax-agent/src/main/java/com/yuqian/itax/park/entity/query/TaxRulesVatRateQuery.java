package com.yuqian.itax.park.entity.query;

import com.yuqian.itax.common.base.entity.query.BaseQuery;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 增值税率列表查询接口
 * @version：1.0
 */
@Getter
@Setter
public class TaxRulesVatRateQuery extends BaseQuery implements Serializable {
    /**
     * 用户id
     */
    private Long memberId;

    /**
     * 企业id
     */
    private Long companyId;

    /**
     * 发票类型 1-增值税普通发票 2-增值税专用发票
     */
    private Integer invoiceType;

    /**
     * 企业开票类目id
     */
    private Long categoryBaseId;
}