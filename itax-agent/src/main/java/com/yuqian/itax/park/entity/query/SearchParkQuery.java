package com.yuqian.itax.park.entity.query;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
public class SearchParkQuery implements Serializable {

    private static final long serialVersionUID = -3273521067150573775L;

    /**
     * 基础类目id
     */
    private Long baseCategoryId;

    /**
     * 经营范围
     */
    private String businessScope;

    /**
     * oem机构编码
     */
    private String oemCode;

    /**
     * 产品id
     */
    private Long productId;

    /**
     * 企业类型 1-个体工商户 2-个人独资企业 3-有限合伙 4-有限责任
     */
    private Integer companyType;
}
