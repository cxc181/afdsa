package com.yuqian.itax.park.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 *
 * @author cxz
 * @date 2022年10月13日 16时52分24秒
 */
@ApiModel(description = "园区返税政策表-修改参数")
@Data
public class ParkTaxRefundPolicyPO implements Serializable {

    @ApiModelProperty(value="主键id",name="id")
    private Long id;

    @ApiModelProperty(value="园区id",name="parkId")
    @NotNull(message = "园区id不能为空")
    private Long parkId;

    @ApiModelProperty(value="税费最小值",name="minValue")
    @NotNull(message = "税费最小值不能为空")
    private Long minValue;

    @ApiModelProperty(value="税费最大值",name="maxValue")
    @NotNull(message = "税费最大值不能为空")
    private Long maxValue;

    @ApiModelProperty(value="返税额的比例",name="rewardRate")
    @NotNull(message = "返税额的比例不能为空")
    @DecimalMin(value = "0.01",message="返税额的比例最小值为0.01")
    private BigDecimal rewardRate;

}
