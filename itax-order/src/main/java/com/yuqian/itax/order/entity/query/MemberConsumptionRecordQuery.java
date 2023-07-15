package com.yuqian.itax.order.entity.query;

import com.yuqian.itax.common.base.entity.query.BaseQuery;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
public class MemberConsumptionRecordQuery extends BaseQuery implements Serializable {

    private static final long serialVersionUID = -1L;

    /**
     * 消费申请订单id
     */
    private Long id;
    /**
     * 订单编号
     */
    private String orderNo;

}
