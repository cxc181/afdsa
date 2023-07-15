package com.yuqian.itax.order.entity.query;

import com.yuqian.itax.common.base.entity.query.BaseQuery;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 *  @Author:
 *  @Date:
 *  @Description: 开票记录详情查询实体类
 */
@Getter
@Setter
public class InvoiceRecordDetailQuery extends BaseQuery implements Serializable {

    /**
     * 订单编号
     */
    @ApiModelProperty(value = "订单编号")
    private String orderNo;

}