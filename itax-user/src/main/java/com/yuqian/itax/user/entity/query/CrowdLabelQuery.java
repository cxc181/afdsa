package com.yuqian.itax.user.entity.query;

import com.yuqian.itax.common.base.entity.query.BaseQuery;
import lombok.Getter;
import lombok.Setter;

/**
 * 人群标签列表查询实体
 */
@Getter
@Setter
public class CrowdLabelQuery extends BaseQuery {

    /**
     * 标签名称
     */
    private String crowdLabelName;

    /**
     * oemCode
     */
    private String oemCode;

    /**
     * 状态 1-正常 2-作废
     */
    private String status;
}
