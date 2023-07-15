package com.yuqian.itax.order.entity.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @Author: yejian
 * @Date: 2020/09/07 10:29
 * @Description: 会员升级创建订单接收DTO
 */
@Getter
@Setter
public class UpgradeOrderDTO implements Serializable {
    private static final long serialVersionUID = -1L;

    /**
     * 订单支付金额
     */
    @NotNull(message = "订单支付金额不能为空")
    @ApiModelProperty(value = "订单支付金额", required = true)
    private Long amount;

    /**
     * 产品ID
     */
    @NotNull(message = "产品ID不能为空")
    @ApiModelProperty(value = "产品ID", required = true)
    private Long productId;

    /**
     * 商品名称
     */
    @ApiModelProperty(value = "商品名称", required = true)
    private String productName;
}