package com.yuqian.itax.product.entity.query;

import com.yuqian.itax.common.base.entity.query.BaseQuery;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 产品管理查询
 * @author：pengwei
 * @Date：2019/12/16 20:12
 * @version：1.0
 */
@Getter
@Setter
public class ProductQuery extends BaseQuery implements Serializable {
    /**
     * 机构状态 0-不可用 1-可用
     */
    private Integer oemStatus;

    /**
     * 机构编码
     */
    private String oemCode;

    /**
     * 产品状态
     */
    private Integer status;

    /**
     * OEM机构
     */
    private String oemName;

    /**
     * 产品类型
     */
    private Integer prodType;

    /**
     * 产品名称
     */
    private String prodName;
}