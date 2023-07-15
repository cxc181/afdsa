package com.yuqian.itax.order.entity.query;

import com.yuqian.itax.common.base.entity.query.BaseQuery;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 *  @Author: yejian
 *  @Date: 2020/05/15 11:09
 *  @Description: 开票补传流水订单查询实体类
 */
@Getter
@Setter
public class InvWaterOrderQuery extends BaseQuery implements Serializable {

    /**
     * 查询类型：1->全部；2->待补传；3->审核中；4->审核通过；5->审核不通过；6->流水前置
     */
    @NotNull(message="查询类型不能为空")
    @ApiModelProperty(value = "查询类型：1->全部；2->待补传；3->审核中；4->审核通过",required = true)
    private Long type;

}