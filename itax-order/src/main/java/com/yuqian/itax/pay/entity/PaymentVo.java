package com.yuqian.itax.pay.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Getter
@Setter
@ApiModel(description = "线下打款-传递参数")
public class PaymentVo implements Serializable {

    private static final long serialVersionUID = 3367575827125132591L;


    @ApiModelProperty(value="支付流水表主键id",name="id")
    @NotNull(message = "支付流水id不能为空")
    private Long id;

    @ApiModelProperty(value="打款凭证",name="payPic")
    @NotBlank(message = "打款凭证不能为空")
    private String payPic;

    @ApiModelProperty(value="描述",name="remark")
    private String remark;

}
