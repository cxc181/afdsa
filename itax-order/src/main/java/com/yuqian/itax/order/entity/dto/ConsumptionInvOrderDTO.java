package com.yuqian.itax.order.entity.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * @Description 创建消费开票订单DTO
 * @Author  Kaven
 * @Date   2020/9/27 14:14
*/
@Getter
@Setter
public class ConsumptionInvOrderDTO implements Serializable {
	
	private static final long serialVersionUID = -1L;

    // 当前登录人ID
    private Long currUserId;

    // 消费订单号，多个用逗号隔开
    @NotEmpty(message = "消费订单号不能为空")
    private String consumptionOrderRela;

    // 机构编码
    private String oemCode;

    // 操作小程序来源 1-微信小程序 2-支付宝小程序
    private Integer sourceType;

    /**
     * 发票抬头ID
     */
    @NotNull(message = "发票抬头不能为空")
    @ApiModelProperty(value = "发票抬头ID")
    private Long invoiceHeadId;

    /**
     * 开票总金额
     */
    @NotNull(message = "开票总金额不能为空")
    @ApiModelProperty(value = "开票总金额")
    private Long totalAmount;

    // 收票邮箱
    @Size(max = 30, message = "收票邮箱不能超过30位字符")
    @Email(message = "收票邮箱格式有误", regexp = "^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$")
    private String billToEmail;

    /**
     * 发票方式 1-纸质发票 2-电子发票
     */
    @NotNull(message = "发票方式不能为空")
    private Integer invoiceWay;

    /**
     * 会员收件地址ID
     */
    private Long memberAddressId;

    /**
     * 一般纳税人资质证明
     */
    private String generalTaxpayerQualification;
}
