package com.yuqian.itax.order.entity.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 开票快递信息
 */
@Getter
@Setter
public class InvCourierVo implements Serializable {

    private static final long serialVersionUID = -1L;

    @ApiModelProperty(value = "订单编号")
    private String orderNo;

    @ApiModelProperty(value = "抬头收件人")
    private String recipient;

    @ApiModelProperty(value = "快递单号")
    private String courierNumber;

    @ApiModelProperty(value = "快递公司名称")
    private String courierCompanyName;

    @ApiModelProperty(value = "企业名称")
    private String companyName;
}
