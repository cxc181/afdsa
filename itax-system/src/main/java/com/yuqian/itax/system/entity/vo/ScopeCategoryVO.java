package com.yuqian.itax.system.entity.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

/**
 * @Description 开票类目经营范围返回bean（纯API）
 * @Author  Kaven
 * @Date   2020/7/31 09:07
*/
@Getter
@Setter
public class ScopeCategoryVO implements Serializable {

    private static final long serialVersionUID = -1L;
    /**
     * 开票类目列表
     */
    @ApiModelProperty(value = "开票类目列表")
    private List<InvoiceCategoryVO> invoiceCategoryList;

    /**
     * 经营范围
     */
    @ApiModelProperty(value = "经营范围")
    private List<BusinessScopeVO> businessScopelist;

    /**
     * 核定税种
     */
    @ApiModelProperty(value = "核定税种")
    private List<RatifyTaxVO> ratifyTaxList;
}