package com.yuqian.itax.system.entity.vo;

import com.yuqian.itax.system.entity.BusinessScopeEntity;
import com.yuqian.itax.system.entity.InvoiceCategoryEntity;
import com.yuqian.itax.system.entity.RatifyTaxEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.jeecgframework.poi.excel.annotation.Excel;

import java.io.Serializable;
import java.util.List;

/**
 *  @Author: Kaven
 *  @Date: 2019/12/19 10:35
 *  @Description: 核定税种/开票类目/经营范围合并VO
 */
@Getter
@Setter
public class IndustryInfoVO implements Serializable {

    private static final long serialVersionUID = -1L;

    /**
     * 行业主键id
     */
    private Long id;

    /**
     * 开票类目列表
     */
    @ApiModelProperty(value = "开票类目列表",required = true)
    private List<InvoiceCategoryEntity> invoiceCategoryList;

    /**
     * 核定税种
     */
    @ApiModelProperty(value = "核定税种",required = true)
    private List<RatifyTaxEntity> ratifyTaxList;

    /**
     * 经营范围
     */
    @ApiModelProperty(value = "经营范围",required = true)
    private List<BusinessScopeEntity> businessScopelist;

    /**
     * 企业类型 1-个体户 2-个人独资企业 3-有限合伙公司 4-有限责任公司
     */
    @Excel(name = "企业类型", replace = { "个体户_1","个人独资企业_2","有限合伙公司_3","有限责任公司_4"})
    private Integer companyType;
    /**
     * 服务类别
     */
    @Excel(name = "服务类别", height = 25, width = 22)
    private String industryName;
    /**
     * 名称示例
     */
    @ApiModelProperty(value = "名称示例",required = true)
    @Excel(name = "商户名称", height = 10, width = 22)
    private String exampleName;

    /**
     * 经营范围
     */
    @Excel(name = "经营范围", height = 10, width = 22)
    private  String businessContent;

    /**
     * 核定税种
     */
    @Excel(name = "核定税种", height = 10, width = 22)
    private String  taxName;
    /**
     * 开票类目
     */
    @Excel(name = "开票类目", height = 10, width = 40)
    private String categoryNames;

    /**
     * 其他说明
     */
    @Excel(name = "其他说明", height = 10, width = 22)
    private String orderDesc;

    /**
     * 发票样例（短）
     */
    private String exampleInvoice;
    /**
     * 发票样例（长）
     */
    private String exampleInvoiceUrl;

    /**
     * 示例名称头部（园区所属城市）
     */
    private String exampleNameHead;

    /**
     * 示例名称尾部（行业名称）
     */
    private String exampleNameTail;
}