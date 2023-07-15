package com.yuqian.itax.order.entity.vo;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;

@Setter
@Getter
public class ConsumptionInvoiceReceivingVO implements Serializable {

    private static final long serialVersionUID = -1L;

    /**
     * 订单编号
     */
    private String orderNo;

    /**
     * 订单类型
     */
    private Integer orderType;

    /**
     * 开票金额
     */
    private BigDecimal invoiceAmount;

    /**
     * 抬头省名称
     */
    private String provinceName;

    /**
     * 抬头市名称
     */
    private String cityName;

    /**
     * 抬头区名称
     */
    private String districtName;

    /**
     * 抬头详细名称
     */
    private String recipientAddress;

    /**
     * 抬头联系方式
     */
    private String recipientPhone;

    /**
     * 抬头收件人
     */
    private String recipient;

    /**
     * 快递单号
     */
    private String courierNumber;

    /**
     * 快递公司名称
     */
    private String courierCompanyName;
}
