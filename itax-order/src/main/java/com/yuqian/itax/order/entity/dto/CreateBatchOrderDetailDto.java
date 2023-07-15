package com.yuqian.itax.order.entity.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * 订单落地通知对象
 * @author：lmh
 * @Date：2023/02/21
 * @version：1.0
 */
@Getter
@Setter
public class CreateBatchOrderDetailDto {

    /**
     * 业务处理结果 SUCCESS成功 FAIL失败
     */
    private String result_code;

    /**
     * 易税批次ID
     */
    private String enterprise_order_id;

    /**
     * 批次类型：S 小额 L 大额 M 超小额
     */
    private String states;
}
