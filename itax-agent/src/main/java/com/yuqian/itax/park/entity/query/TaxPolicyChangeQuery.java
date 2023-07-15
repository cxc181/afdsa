package com.yuqian.itax.park.entity.query;

import com.yuqian.itax.common.base.entity.query.BaseQuery;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class TaxPolicyChangeQuery extends BaseQuery implements Serializable {

    /**
     * 园区id
     */
    private Long  parkId;

    /**
     * 园区政策id
     */
    private Long policyId;

    /**
     * 类型 1-税费政策 2-其他政策
     */
    private Integer type;
}
