package com.yuqian.itax.order.entity.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 订单落地通知对象
 * @author：lmh
 * @Date：2023/02/21
 * @version：1.0
 */
@Getter
@Setter
public class CreateBatchOrderDto {

    /**
     * 对应提现订单编号
     */
    private String trade_number;

    /**
     * 提现订单明细
     */
    private List<CreateBatchOrderDetailDto> data;
}
