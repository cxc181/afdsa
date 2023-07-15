package com.yuqian.itax.yishui.entity;

import lombok.Data;

/**
 * @author Administrator
 */
@Data
public class Pagination {

    /**
     * 起始页
     */
    private Integer page_start = 1;
    /**
     * 页数量
     */
    private Integer page_size = 10;
}
