package com.yuqian.itax.order.entity.query;

import com.yuqian.itax.common.base.entity.query.BaseQuery;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 *  @Author: lmh
 *  @Date: 2021/8/12
 *  @Description: 开户订单查询实体类-接入方使用
 */
@Getter
@Setter
public class AccessPartyOrderQuery extends BaseQuery implements Serializable {
    /**
     * 机构编码
     */
    private String oemCode;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 接入方编码
     */
    private String accessPartyCode;

    /**
     * 订单统计状态 1-所有状态 2-未支付状态 3-未完成状态（已支付未完成）
     */
    private Integer orderStatisticsStatus;

    /**
     * 订单编号
     */
    private String orderNo;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 用户名称
     */
    private String userName;

    /**
     * 业务来源单号
     */
    private String externalOrderNo;
}