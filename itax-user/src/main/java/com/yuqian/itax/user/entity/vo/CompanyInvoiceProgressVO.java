package com.yuqian.itax.user.entity.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @Description 企业开票进度跟进统计展示VO
 * @Author  Kaven
 * @Date   2020/6/6 4:33 下午
*/
@Getter
@Setter
public class CompanyInvoiceProgressVO implements Serializable {
    private static final long serialVersionUID = -1L;

    /**
     * 待提交订单数
     */
    @ApiModelProperty(value = "待提交订单数")
    private Integer toBeSubmitCount;

    /**
     * 审核中订单数
     */
    @ApiModelProperty(value = "审核中订单数")
    private Integer toBeCheckCount;

    /**
     * 待签收订单数
     */
    @ApiModelProperty(value = "待签收订单数")
    private Integer toReceiptCount;

    /**
     * 已完成订单数
     */
    @ApiModelProperty(value = "已完成订单数")
    private Integer finishedCount;
}
