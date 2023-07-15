package com.yuqian.itax.park.entity.query;

import com.yuqian.itax.common.base.entity.query.BaseQuery;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class ParkDisableWordQuery extends BaseQuery {

    private static final long serialVersionUID = -1L;

    /**
     * 园区id
     */
    private Long parkId;

    /**
     * 禁用字
     */
    private String disableWord;

    /**
     * 创建开始时间
     */
    private Date addTimeBeg;

    /**
     * 创建结束时间
     */
    private Date  addTimeEnd;
}
