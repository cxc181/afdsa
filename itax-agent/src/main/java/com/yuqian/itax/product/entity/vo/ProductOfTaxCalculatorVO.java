package com.yuqian.itax.product.entity.vo;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class ProductOfTaxCalculatorVO implements Serializable {

    /**
     * 产品id
     */
    private Long productId;

    /**
     * 企业类型 2-个人独资企业 3-有限合伙 4-有限责任
     */
    private Integer companyType;

    /**
     * 企业经营性质
     */
    private String companyBusinessNature;
}
