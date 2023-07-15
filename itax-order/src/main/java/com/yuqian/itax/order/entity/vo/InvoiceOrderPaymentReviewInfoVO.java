package com.yuqian.itax.order.entity.vo;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 开票订单财务审核信息
 */
@Getter
@Setter
public class InvoiceOrderPaymentReviewInfoVO implements Serializable {

    private static final long serialVersionUID = -1L;

    /**
     * 订单编号
     */
    private String orderNo;

    /**
     * 企业id
     */
    private Long companyId;

    /**
     * 公司名称
     */
    private String companyName;

    /**
     *开票金额
     */
    private Long invoiceAmount;

    /**
     *支付金额
     */
    private Long payAmount;

    /**
     * 支付凭证
     */
    private String paymentVoucher;

    /**
     * 授权后图片数组
     */
    private List<String> paymentVoucherList = new ArrayList<>();
}
