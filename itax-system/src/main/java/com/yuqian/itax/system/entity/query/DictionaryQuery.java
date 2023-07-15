package com.yuqian.itax.system.entity.query;

import com.yuqian.itax.common.base.entity.query.BaseQuery;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class DictionaryQuery extends BaseQuery {


    /**
     *
     */
    Long id;
    /**
     * 编码
     */
    String dictCode;

    /**
     * 上级Code
     */
    String parentDictCode;
}
