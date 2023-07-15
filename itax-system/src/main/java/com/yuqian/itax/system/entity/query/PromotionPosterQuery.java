package com.yuqian.itax.system.entity.query;

import com.yuqian.itax.common.base.entity.query.BaseQuery;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class PromotionPosterQuery extends BaseQuery {

    private static final long serialVersionUID = -1L;

    /**
     * 主键id
     */
    private Long id;

    /**
     * 海报名称
     */
    private String posterName;

    /**
     * 排序号
     */
    private Integer posterSn;

    /**
     * 机构名称
     */
    private String oemName;

}
