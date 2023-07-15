package com.yuqian.itax.corporateaccount.entity.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

/**
 * 对公户申请订单返回VO
 *
 * @author yejian
 * @Date: 2020年09月09日 14:11:04
 */
@Getter
@Setter
public class CorporateAccountApplyOrderVO implements Serializable {

    private static final long serialVersionUID = -1L;

    /**
     * 订单号
     */
    @ApiModelProperty(value = "订单号")
    private String orderNo;

    /**
     * 企业名称
     */
    @ApiModelProperty(value = "企业名称")
    private String companyName;

    /**
     * 申请银行
     */
    @ApiModelProperty(value = "申请银行")
    private String applyBankName;

    /**
     * 状态 0-待支付 1-等待预约 2-已完成 3-已取消
     */
    @ApiModelProperty(value = "状态 0-待支付 1-等待预约 2-已完成 3-已取消")
    private Integer orderStatus;

    /**
     * 添加时间
     */
    @ApiModelProperty(value = "添加时间")
    private Date addTime;

    /**
     * 支付金额
     */
    @ApiModelProperty(value = "支付金额")
    private Long payAmount;

    /**
     * 产品名称
     */
    @ApiModelProperty(value = "产品名称")
    private String goodsName;

    /**
     * 银行总部名称
     */
    @ApiModelProperty(value = "银行总部名称")
    private String headquartersName;

}
