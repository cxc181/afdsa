package com.yuqian.itax.system.entity.query;

import com.yuqian.itax.common.base.entity.query.BaseQuery;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class InvoiceCategoryBaseQuery extends BaseQuery {
    /**
     * 类目主键id
     */
    Long id;
    /**
         * 商品名称
     */
    String goodsName;
    /**
     * 税收分类简称
     */
    String taxClassificationAbbreviation;
    /**
     * 税收分类名称
     */
    String taxClassificationName;
    /**
     * 税收分类编码
     */
    String taxClassificationCode;
    /**
     * 企业id
     */
    Long companyId;
}
