package com.yuqian.itax.park.entity.vo;

import lombok.Getter;
import lombok.Setter;


import java.io.Serializable;

/**
 * 商品编码匹配园区经营范围VO
 */
@Setter
@Getter
public class ParkBusinessScopeWithTaxCodeVO implements Serializable {

    /**
     * 商品编码
     */
    private String taxClassificationCode;

    /**
     * 园区经营范围id
     */
    private Long parkBusinessscopeId;

    /**
     * 经营范围名称
     */
    private String businessscopeName;

    /**
     * 经营范围基础库id
     */
    private Long businessscopeBaseId;

    /**
     * 商品名称
     */
    private String goodsName;
}
