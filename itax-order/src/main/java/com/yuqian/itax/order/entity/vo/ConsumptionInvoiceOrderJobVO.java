package com.yuqian.itax.order.entity.vo;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
public class ConsumptionInvoiceOrderJobVO implements Serializable {

    private static final long serialVersionUID = -1L;

    /**
     * 订单号
     */
    private String orderNo;

    /**
     * 快递单号
     */
    private String courierNumber;

    /**
     * 快递公司名称
     */
    private String courierCompanyName;

    /**
     * 订单状态
     */
    private Integer orderStatus;

    /**
     * 修改时间
     */
    private Date updateTime;
}
