package com.yuqian.itax.product.entity.vo;

import io.swagger.models.auth.In;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Getter
@Setter
public class ProductCrowdParkVO implements Serializable {

    private static final long serialVersionUID = -1L;

    /**
     * oemCode
     */
    private String oemCode;

    /**
     * oem机构
     */
    private String oemName;
    /**
     * 活动名称
     */
    private String activityName;

    /**
     * 人群标签id
     */
    private List<Long> crowdLabelIds;

    /**
     * 活动id
     */
    private Long activityId;

    /**
     * 园区id
     */
    private List<Long> parkIds;

    /**
     * 行业id
     */
    private List<Long> industryIds;

    /**
     * 活动开始时间
     */
    private Date activityStartDate;

    /**
     * 活动结束时间
     */
    private Date activityEndDate;

    /**
     * 活动类型
     */
    private Integer productType;

    /**
     * 特价金额(分)
     */
    private Long specialPriceAmount;

    /**
     * 办理费（对公户独有）
     */
    private Long processingFee;

    /**
     * 服务费阶梯
     */
    private List<DiscountActivityChangeVO> chargeStandard;

    /**
     * 注销累计开票额度
     */
    private Long cancelTotalLimit;
}
