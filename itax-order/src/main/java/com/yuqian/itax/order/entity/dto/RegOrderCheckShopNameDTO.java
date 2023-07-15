package com.yuqian.itax.order.entity.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 *  @Author: lmh
 *  @Date: 2021/10/21
 *  @Description: 工商注册订单字号校验接收DTO
 */
@Getter
@Setter
public class RegOrderCheckShopNameDTO implements Serializable {
    private static final long serialVersionUID = -1L;

    @NotNull(message = "园区id不能为空")
    @ApiModelProperty(value = "园区id",required = true)
    private Long parkId;

    @NotBlank(message = "字号不能为空")
    @ApiModelProperty(value = "字号",required = true)
    private String shopName;

    @ApiModelProperty(value = "备选字号1",required = true)
    private String shopNameOne;

    @ApiModelProperty(value = "备选字号2",required = true)
    private String shopNameTwo;
}