package com.yuqian.itax.order.entity.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

/**
 * @Description 消费订单列表VO
 * @Author yejian
 * @Date 2020/9/28 14:51
 */
@Getter
@Setter
public class ConsumptionRelaOrderVO implements Serializable {

    private static final long serialVersionUID = -1L;

    /**
     * 添加时间
     */
    @ApiModelProperty(value = "添加时间")
    private Date addTime;

    /**
     * 订单类型: 3-提现 4-代理提现 5-工商注册 6-开票 7-用户升级 8-工商注销 9-证件申请 10-对公户申请 11-对公户提
     */
    @ApiModelProperty(value = "订单类型: 3-提现 4-代理提现 5-工商注册 6-开票 7-用户升级 8-工商注销 9-证件申请 10-对公户申请 11-对公户提")
    private Integer orderType;

    /**
     * 消费金额
     */
    @ApiModelProperty(value = "消费金额")
    private Long consumptionAmount;
}
