package com.yuqian.itax.order.entity.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @Description 订单确认
 * @Author  shudu
 * @Date   2021/05/06 10:22
 */
@Getter
@Setter
public class InvoiceConfirmDTO implements Serializable {

    private static final long serialVersionUID = -1L;

    /**
     * 提现结果
     */
    @ApiModelProperty(value = "提现结果",required = true)
    private String result;

    /**
     * 订单编号
     */
    @ApiModelProperty(value = "订单编号",required = true)
    private String orderNo;
}
