package com.yuqian.itax.order.entity.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;

@Setter
@Getter
public class ThirdPartyAddPaymentVoucherDTO implements Serializable {
    private static final long serialVersionUID = -1L;

    /**
     * 机构编码
     */
    private String oemCode;
    /**
     * 接入方编码
     */
    private String accessPartyCode;
    /**
     * 订单编号
     */
    @NotBlank(message = "订单编号不能为空")
    private String orderNo;
    /**
     * 支付凭证
     */
    @NotBlank(message = "支付凭证不能为空")
    private String paymentVoucher;
}
