package com.yuqian.itax.order.entity.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * @Description: 开票订单补传流水DTO
 * @Author: yejian
 * @Date: 2020/05/18 09:29
 */
@Getter
@Setter
public class InvOrderBankWaterApiDTO implements Serializable {

    private static final long serialVersionUID = -1L;

    /**
     * 订单号
     */
    @NotBlank(message = "订单号不能为空")
    @ApiModelProperty(value = "订单号", required = true)
    private String orderNo;

    /**
     *
     */
    @NotBlank(message = "会员账号不能为空")
    @ApiModelProperty(value = "会员账号", required = true)
    private String regPhone;

    /**
     * 银行流水截图（多张逗号拼接）
     */
    @NotBlank(message = "银行流水截图不能为空")
    @ApiModelProperty(value = "银行流水截图（多张逗号拼接）")
    private String accountStatement;

}