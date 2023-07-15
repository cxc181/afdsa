package com.yuqian.itax.error.entity.query;

import com.yuqian.itax.common.base.entity.query.BaseQuery;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ErrorCodeQuery extends BaseQuery {

    /**
     * 错误代码
     */
    private String errorCode;

    /**
     * 是否转译 0-否 1-是
     */
    private Integer isTranslation;

    /**
     * 错误类型  1-参数错误 2-业务错误 3-数据库错误
     */
    private Integer errorType;
}
