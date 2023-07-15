package com.yuqian.itax.order.entity.query;

import com.yuqian.itax.common.base.entity.query.BaseQuery;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * @Description 消费开票订单详情查询BEAN
 * @Author yejian
 * @Date 2020/9/28 14:44
 */
@Getter
@Setter
public class ConsumptionRecordDetailQuery extends BaseQuery implements Serializable {

    /**
     * 关联消费订单号,多个订单之间用逗号分割
     */
    @NotBlank(message = "关联消费订单号不能为空")
    @ApiModelProperty(value = "关联消费订单号,多个订单之间用逗号分割", required = true)
    private String consumptionOrderRela;

}