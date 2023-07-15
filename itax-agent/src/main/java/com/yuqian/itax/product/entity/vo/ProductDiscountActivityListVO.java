package com.yuqian.itax.product.entity.vo;

import lombok.Getter;
import lombok.Setter;
import org.jeecgframework.poi.excel.annotation.Excel;

import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
public class ProductDiscountActivityListVO implements Serializable {

    private static final long serialVersionUID = -1L;

    /**
     * 主键id
     */
    @Excel(name = "活动编码")
    private Long id;

    /**
     * 活动名称
     */
    @Excel(name = "活动名称")
    private String activityName;

    /**
     * 适用人群
     */
    @Excel(name = "适用人群")
    private String crowdLabelName;

    /**
     * 活动产品
     */
    @Excel(name = "活动产品", replace = { "null_-","个体开户_1","个体开票_5","个体注销_11","公户申请和托管_15","个体托管费续费_16" }, height = 10, width = 22)
    private String productType;

    /**
     * 状态
     */
    @Excel(name = "状态", replace = { "null_-","待上架_0","已上架_1","已下架_2","已暂停_3"}, height = 10, width = 22)
    private Integer status;

    /**
     *活动结束日期
     */
    private Date activityEndDate;


    /**
     * 活动开始日期
     */
    private Date activityStartDate;

    /**
     * 有效期
     */
    @Excel(name = "有效期")
    private String termOfValidity;

    /**
     * oem机构
     */
    @Excel(name = "OEM机构")
    private String oemName;

    /**
     * 添加人
     */
    @Excel(name = "创建人")
    private String addUser;

    /**
     * 更新人
     */
    @Excel(name = "最近更新人")
    private String updateUser;
}
