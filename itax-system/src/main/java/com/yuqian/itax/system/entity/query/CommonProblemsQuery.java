package com.yuqian.itax.system.entity.query;

import com.yuqian.itax.common.base.entity.query.BaseQuery;
import lombok.Getter;
import lombok.Setter;

/**
 * 常见问题查询条件
 */
@Getter
@Setter
public class CommonProblemsQuery extends BaseQuery {

    /**
     * 排序
     */
    private int orderNum;

    /**
     * 问题
     */
    private String problem;
    /**
     * oem机构（模糊查询）
     */
    private String oemName;

    /**
     * 机构编码
     */
    private  String oemCode;
}
