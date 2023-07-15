package com.yuqian.itax.system.entity.vo;

import lombok.Getter;
import lombok.Setter;
import org.jeecgframework.poi.excel.annotation.Excel;

import java.io.Serializable;

@Getter
@Setter
public class BusinessScopeBatchVO implements Serializable {

    /**
     * 经营范围
     */
    @Excel(name = "经营范围")
    private String businessScopName;

    /**
     * 税收分类编码
     */
    @Excel(name = "税收分类编码")
    private String taxClassificationCode;

    /**
     * 税费分类名称
     */
    @Excel(name = "税收分类名称")
    private String taxClassificationName;

    /**
     * 备注
     */
    @Excel(name = "备注")
    private String remark;

    @Excel(name = "失败原因")
    private String  msg;

    private boolean falg = true;

}
