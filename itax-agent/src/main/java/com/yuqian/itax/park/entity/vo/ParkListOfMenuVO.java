package com.yuqian.itax.park.entity.vo;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;

@Setter
@Getter
public class ParkListOfMenuVO implements Serializable {
    private static final long serialVersionUID = -1L;

    /**
     * 园区id
     */
    private Long parkId;

    /**
     * 园区名称
     */
    private String parkName;

    /**
     * 园区所在地
     */
    private String parkCity;

    /**
     * 园区类型 1-自营园区  2-合作园区 3-外部园区
     */
    private Integer parkType;

    /**
     * 用户评分
     */
    private BigDecimal userRatings;

    /**
     * 交易订单数
     */
    private Long orderNumber;

    /**
     * 园区预览图
     */
    private String parkThumbnail;

    /**
     * 支持的企业类型
     */
    private String companyTypes;

    /**
     * 政策标签
     */
    private String policyLabels;

    /**
     * 征收方式标签
     */
    private String incomeLevyTypeLabel;
}
