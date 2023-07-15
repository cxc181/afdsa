package com.yuqian.itax.agreement.entity.query;

import com.yuqian.itax.common.base.entity.query.BaseQuery;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

/**
 * 园区协议列表查询query
 */
@Getter
@Setter
public class ParkAgreementsQuery extends BaseQuery {

    /**
     * 园区id
     */
    @NotNull(message = "园区id不能为空")
    private Long parkId;

    /**
     * 模板类型  1-收费标准 2-委托注册协议  3-园区办理协议
     */
    private Integer templateType;

    /**
     * 模板状态  1-启用 2-禁用
     */
    private Integer templateStatus;

    /**
     * 机构编码（传机构编码时，查询到的是产品统一配置的模板，不传时，查询到的是园区单独配置）
     */
    private String oemCode;

    /**
     * 产品id
     */
    @NotNull(message = "产品id不能为空")
    private Long productId;

    /**
     * 企业类型
     */
    @NotNull(message = "企业类型不能为空")
    private Integer companyType;
}
