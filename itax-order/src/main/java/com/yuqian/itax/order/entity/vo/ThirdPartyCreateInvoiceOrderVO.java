package com.yuqian.itax.order.entity.vo;

import lombok.Getter;
import lombok.Setter;
import org.jeecgframework.poi.excel.annotation.Excel;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 接入方开票创建订单返回信息
 * @author：hz
 * @Date：2021/9/9 18:52
 * @version：1.0
 */
@Getter
@Setter
public class ThirdPartyCreateInvoiceOrderVO implements Serializable {
    /**
     * 订单号
     */
    private String orderNo;
    /**
     * 未支付的开票订单编号
     */
    private String noPayOrderNo;
    /**
     * 应缴税费
     */
    private Long payAmount;

    /**
     * 应缴服务费
     */
    private Long serviceFee;
    /**
     * 快递费
     */
    private Long postageFees;

    /**
     * 业务来源单号
     */
    private String externalOrderNo;
}
