package com.yuqian.itax.snapshot.entity.query;

import com.yuqian.itax.common.base.entity.query.BaseQuery;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 园区交易量统计query
 */

@Setter
@Getter
public class InvoiceOrderSnapshotParkQuery extends BaseQuery implements Serializable {
    private static final long serialVersionUID = -1L;

    /**
     * 园区ID
     */
    private Long parkId;
}
