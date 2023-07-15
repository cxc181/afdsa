package com.yuqian.itax.product.entity.vo;

import com.yuqian.itax.product.entity.DiscountActivityChargeStandardRelaEntity;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Getter
@Setter
public class ProductActivityDetailVO implements Serializable {

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
     * 活动id
     */
    private String activityid;

    /**
     * 活动名称
     */
    private String activityName;

    /**
     * 人群id
     */
    private String crowdLabelIds;

    /**
     * 园区id
     */
    private String parkIds;

    /**
     * 行业id
     */
    private String industryIds;

    /**
     * 产品类型
     */
    private Integer productType;

    /**
     * 特价金额
     */
    private BigDecimal specialPriceAmount;

    /**
     * 开始时间
     */
    private Date activityStartDate;

    /**
     * 结束时间
     */
    private Date activityEndDate;

    /**
     * 办理费（对公户独有）
     */
    private Long processingFee;

    /**
     * 注销累计开票额度
     */
    private Long cancelTotalLimit;

    /**
     * 特价活动开票服务费标准id
     */
    private String chargeStandard;

    /**
     * 开票服务费标准
     */
    private List<DiscountActivityChargeStandardRelaEntity>  charge;
}
