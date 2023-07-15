package com.yuqian.itax.system.entity.vo;

import lombok.Getter;
import lombok.Setter;
import org.jeecgframework.poi.excel.annotation.Excel;

import java.io.Serializable;

/**
 * 未匹配到的经营范围
 */
@Getter
@Setter
public class UnmatchedBusinessScopeVO implements Serializable {

    /**
     * 税收分类编码
     */
    @Excel(name = "税收分类编码")
    private String taxClassificationCode;

    /**
     * 税费分类名称
     */
    @Excel(name = "税收分类名称")
    private String goodsName;
}
