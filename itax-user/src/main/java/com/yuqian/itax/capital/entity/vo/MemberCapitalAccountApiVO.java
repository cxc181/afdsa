package com.yuqian.itax.capital.entity.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @Author: yejian
 * @Date: 2020/11/13 11:15
 * @Description: 我的钱包api展示bean
 */
@Getter
@Setter
public class MemberCapitalAccountApiVO implements Serializable {

    private static final long serialVersionUID = -1L;

    /**
     * 可用余额
     */
    @ApiModelProperty(value = "可用余额")
    private Long usableAmount;

    /**
     * 待结算金额
     */
    @ApiModelProperty(value = "待结算金额")
    private Long outstandingAmount;
}
