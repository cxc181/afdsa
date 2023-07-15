package com.yuqian.itax.park.entity.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;

@ApiModel(description = "园区返税政策表-响应参数")
@Data
public class ParkTaxRefundPolicyVO implements Serializable {

    private static final long serialVersionUID = -1L;

    @ApiModelProperty(value="主键id",name="id")
    private Long id;

    @ApiModelProperty(value="园区id",name="parkId")
    private Long parkId;

    @ApiModelProperty(value="税费最小值（分）",name="minValue")
    private Long minValue;

    @ApiModelProperty(value="税费最大值（分）",name="maxValue")
    private Long maxValue;

    @ApiModelProperty(value="奖励比例，小数",name="rewardRate")
    private BigDecimal rewardRate;

}
