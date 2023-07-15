package com.yuqian.itax.agent.entity.vo;


import lombok.Getter;
import lombok.Setter;
import org.jeecgframework.poi.excel.annotation.Excel;

import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
public class InvoiceCategoryBaseStringAgentVO implements Serializable {

    private static final long serialVersionUID = -1L;
    /**
     * 类目基础表主键id
     */
    private Long id;

    /**
     * 税收分类编码
     */
    @Excel(name = "税收分类编码")
    private String taxClassificationCode;

    /**
     * 税收分类名称
     */
    @Excel(name = "税收分类名称")
    private String taxClassificationName;

    /**
     * 税收分类简称
     */
    @Excel(name = "税收分类简称")
    private String taxClassificationAbbreviation;

    /**
     * 商品名称
     */
    @Excel(name = "商品名称")
    private String goodsName;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InvoiceCategoryBaseStringAgentVO that = (InvoiceCategoryBaseStringAgentVO) o;
        return Objects.equals(taxClassificationAbbreviation, that.taxClassificationAbbreviation) &&
                Objects.equals(goodsName, that.goodsName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(taxClassificationAbbreviation, goodsName);
    }
}