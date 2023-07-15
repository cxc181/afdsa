package com.yuqian.itax.order.entity.query;

import com.yuqian.itax.common.base.entity.query.BaseQuery;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

/**
 * @Description 选择消费开票订单查询BEAN
 * @Author  Kaven
 * @Date   2020/9/27 10:04
*/
@Getter
@Setter
public class ConsumptionRecordQuery extends BaseQuery implements Serializable {
    /**
     * 用户id
     */
    private Long currUserId;

    /**
     * 订单类型
     */
    @ApiModelProperty(value = "订单类型",required = true)
    private Integer orderType;

    /**
     * 完成时间
     */
    @ApiModelProperty(value = "完成时间")
    private Date addTime;

    private String month; // 按月份查询

    private String day;// 按日查询

}