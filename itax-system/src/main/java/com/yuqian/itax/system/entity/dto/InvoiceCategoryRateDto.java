package com.yuqian.itax.system.entity.dto;

import lombok.Getter;
import lombok.Setter;
import org.jeecgframework.poi.excel.annotation.Excel;
import java.io.Serializable;

/**
 *  @Author: cxz
 *  @Description: 类目库增值税率导入模板DTO
 */
@Getter
@Setter
public class InvoiceCategoryRateDto implements Serializable {

    private static final long serialVersionUID = -1L;

    @Excel(name = "税收分类编码")
    private String taxClassificationCode;

    @Excel(name = "增值税率(%)")
    private String vatFeeRate;

    /**
     * 失败原因
     */
    @Excel(name = "失败原因")
    private String failed;

}
