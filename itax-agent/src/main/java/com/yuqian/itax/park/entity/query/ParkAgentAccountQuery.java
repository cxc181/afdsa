package com.yuqian.itax.park.entity.query;

import com.yuqian.itax.common.base.entity.query.BaseQuery;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ParkAgentAccountQuery extends BaseQuery {

    private static final long serialVersionUID = -1L;

    /**
     * 园区id
     */
    Long parkId;

    /**
     * 状态 0-禁用 1-正常  2-已删除
     */
    Integer status;
}
