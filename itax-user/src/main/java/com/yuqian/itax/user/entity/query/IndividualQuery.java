package com.yuqian.itax.user.entity.query;

import com.yuqian.itax.common.base.entity.query.BaseQuery;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class IndividualQuery extends BaseQuery {

    /**
     * 机构编码
     */
    private String oemCode;
}
