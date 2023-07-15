package com.yuqian.itax.user.entity.vo;

import io.swagger.annotations.ApiModelProperty;
import io.swagger.models.auth.In;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @Description 企业注册进度跟进统计展示VO
 * @Author  Kaven
 * @Date   2020/6/6 3:40 下午
*/
@Getter
@Setter
public class CompanyRegProgressVO implements Serializable {
    private static final long serialVersionUID = -1L;

    /**
     * 未注册企业数
     */
    @ApiModelProperty(value = "未注册企业数")
    private Integer unRegComCount;

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
     * 核名驳回订单数
     */
    @ApiModelProperty(value = "核名驳回订单数")
    private Integer toDismissCount;

    /**
     * 出证中订单数
     */
    @ApiModelProperty(value = "出证中订单数")
    private Integer toCheckoutCount;

    /**
     * 已完成订单数
     */
    @ApiModelProperty(value = "已完成订单数")
    private Integer finishedCount;

    /**
     * 总托管费
     */
    @ApiModelProperty(value = "总托管费")
    private Long totalRegistFee;
}
