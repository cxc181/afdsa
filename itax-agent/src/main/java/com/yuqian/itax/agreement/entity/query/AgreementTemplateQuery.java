package com.yuqian.itax.agreement.entity.query;

import com.yuqian.itax.common.base.entity.query.BaseQuery;
import lombok.Getter;
import lombok.Setter;

/**
 * 协议模板查询
 */
@Getter
@Setter
public class AgreementTemplateQuery extends BaseQuery {

    /**
     * 协议模板查询
     */
    private String templateName;

    /**
     * 模板类型  1-收费标准 2-委托注册协议  3-园区办理协议
     */
    private Integer templateType;

    /**
     * 模板状态  1-启用 2-禁用
     */
    private Integer templateStatus;
}
