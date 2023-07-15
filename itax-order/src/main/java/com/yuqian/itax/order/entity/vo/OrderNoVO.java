package com.yuqian.itax.order.entity.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 *  @Author: yejian
 *  @Date: 2020/04/20 11:34
 *  @Description: 订单号实体类
 */
@Setter
@Getter
public class OrderNoVO {
    /**
     * 订单号
     */
    @ApiModelProperty(value = "订单号")
    private String orderNo;

    /**
     * 发票标识
     */
    private Integer invoiceMark;
}