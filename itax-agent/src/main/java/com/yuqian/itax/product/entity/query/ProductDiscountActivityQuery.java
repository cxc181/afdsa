package com.yuqian.itax.product.entity.query;

import com.yuqian.itax.common.base.entity.query.BaseQuery;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 产品特价活动
 */
@Getter
@Setter
public class ProductDiscountActivityQuery extends BaseQuery implements Serializable {

    /**
     * 活动名称
     */
    private String activityName;

    /**
     * oemCode
     */
    private String oemCode;

    /**
     * 活动产品
     */
    private Integer productType;

    /**
     * 状态
     */
    private Integer status;
}
