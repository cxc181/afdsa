package com.yuqian.itax.order.entity.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 开票截止时间返回
 */
@Getter
@Setter
public class InvoiceEndtimeVO implements Serializable {

    private static final long serialVersionUID = -1L;

    /**
     * 是否在截止时间内 0-否 1-是(在开票截止提示日期内需要提示)
     */
    @ApiModelProperty(value = "是否在截止时间内")
    private Integer atEndtime = 0;

    /**
     * 提示内容
     */
    @ApiModelProperty(value = "提示内容")
    private String content;
}
