package com.yuqian.itax.system.entity.vo;

import lombok.Getter;
import lombok.Setter;
import org.jeecgframework.poi.excel.annotation.Excel;

import java.io.Serializable;
import java.util.Objects;

/**
 * 根据关键词搜索开票类目返回
 */
@Getter
@Setter
public class SearchInvoiceCategoryBaseVO implements Serializable {

    private static final long serialVersionUID = -1L;

    /**
     * 类目基础表主键id
     */
    private Long id;

    /**
     * 开票类目
     */
    private String invoiceCategory;
}
