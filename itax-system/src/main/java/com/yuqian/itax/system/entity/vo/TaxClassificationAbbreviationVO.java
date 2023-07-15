package com.yuqian.itax.system.entity.vo;

import lombok.Getter;
import lombok.Setter;
import org.jeecgframework.poi.excel.annotation.Excel;

import java.io.Serializable;

@Getter
@Setter
public class TaxClassificationAbbreviationVO implements Serializable {

    private static final long serialVersionUID = -1L;
    /**
     * 类目基础表主键id
     */
    private Long id;


    /**
     * 税收分类简称
     */
    private String taxClassificationAbbreviation;
}
