package com.yuqian.itax.agent.entity.query;

import com.yuqian.itax.common.base.entity.query.BaseQuery;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class OemQuery  extends BaseQuery {

    /**
     * 机构名称
     */
    private String oemName;

    /**
     * 机构公司名称
     */
    private String oemCompanyName;

    /**
     * 机构状态 0-下架 1-上架 2-暂停 3-待上架
     */
    private Integer status;

    /**
     * 收单oem机构名称
     */
    private String otherPayOemName;
}
