package com.yuqian.itax.product.entity.vo;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class ProductDiscountActivityOnCrowdLabelVO implements Serializable {

    private static final long serialVersionUID = -1L;

    /**
     * 人群标签id
     */
    private Long crowdLabelId;

    /**
     * 人群标签名称
     */
    private String crowdLabelName;

    /**
     * 特价活动id
     */
    private Long discountActivityId;

    /**
     * 产品名称
     */
    private String activityName;
}
