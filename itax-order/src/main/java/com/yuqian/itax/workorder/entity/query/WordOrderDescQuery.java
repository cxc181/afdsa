package com.yuqian.itax.workorder.entity.query;

import com.yuqian.itax.common.base.entity.query.BaseQuery;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class WordOrderDescQuery extends BaseQuery {

    private static final long serialVersionUID = -1L;

    /**
     * g工单号
     */
    private String workOrderNo;

}
