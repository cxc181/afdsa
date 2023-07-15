package com.yuqian.itax.order.entity.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @Description 订单确认
 * @Author  shudu
 * @Date   2021/05/06 10:22
 */
@Getter
@Setter
public class ThirdPartyInvoiceConfirmDTO implements Serializable {

    private static final long serialVersionUID = -1L;
    /**
     * 订单编号
     */
    @NotBlank(message = "订单号不能为空")
    private String orderNo;
    /**
     * 机构编码
     */
    private String oemCode;
    /**
     * 接入方编码
     */
    private String accessPartyCode;
    /**
     * 企业id
     */
    @NotNull(message = "企业id不能为空")
    private Long companyId;
    /**
     * 用户id
     */
    @NotNull(message = "用户id不能为空")
    private Long userId;

    /**
     * 确认结果 1-确认支付 2-取消订单
     */
    private Integer confirmResult;

    /**
     * 支付凭证
     */
    private String paymentVoucher;
}
