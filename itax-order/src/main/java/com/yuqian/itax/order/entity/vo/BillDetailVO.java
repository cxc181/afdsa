package com.yuqian.itax.order.entity.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

/**
 * 账单明细返回实体
 * @author：yejian
 * @Date：2019/12/23 14:52
 */
@Getter
@Setter
public class BillDetailVO implements Serializable {

    private static final long serialVersionUID = -1L;

    /**
     * 订单类型：1->充值；2->代理充值；3->提现；4->代理提现；5->工商开户；6->开票；7->用户升级；8->工商注销；9->推广分润；10->退款
     */
    @ApiModelProperty(value = "订单类型")
    private Integer orderType;

    /**
     * 订单类型名称
     */
    @ApiModelProperty(value = "订单类型名称")
    private String orderTypeName;

    /**
     * 加减符号
     */
    @ApiModelProperty(value = "加减符号")
    private String calcType;

    /**
     * 金额
     */
    @ApiModelProperty(value = "金额")
    private Integer amount;

    /**
     * 支付状态：0->待支付；1->支付中；2->支付成功；3->支付失败
     */
    @ApiModelProperty(value = "支付状态：0->待支付；1->支付中；2->支付成功；3->支付失败")
    private String payStatus;

    /**
     * 支付状态名称
     */
    @ApiModelProperty(value = "支付状态名称")
    private String payStatusName;

    /**
     * 添加时间
     */
    @ApiModelProperty(value = "添加时间")
    private Date addTime;

    /**
     * 支付方式 1-微信 2-余额 3-支付宝 4-快捷支付
     */
    @ApiModelProperty(value = "支付方式 1-微信 2-余额 3-支付宝 4-快捷支付")
    private Integer payWay;

}
