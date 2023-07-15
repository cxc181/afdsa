package com.yuqian.itax.profits.entity.query;

import com.yuqian.itax.common.base.entity.query.BaseQuery;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 *  @Author: Kaven
 *  @Date: 2019/12/20 9:28
 *  @Description: 会员分润记录查询bean
 */
@Getter
@Setter
public class MemberProfitsQuery extends BaseQuery implements Serializable {
    private static final long serialVersionUID = -1L;

    /**
     * 用户ID
     */
    @ApiModelProperty(value = "用户ID")
    private Long userId;

    /**
     * OEM机构编码
     */
    @ApiModelProperty(value = "OEM机构编码")
    private String oemCode;

    /**
     *  月份
     */
    @ApiModelProperty(value = "月份")
    private String month;

    /**
     *  开始时间
     */
    @ApiModelProperty(value = "开始时间")
    private String startDate;

    /**
     *  结束时间
     */
    @ApiModelProperty(value = "结束时间")
    private String endDate;

    /**
     * 分润类型(订单类型)  1-会员升级费 2-工商注册（托管）费 3-开票服务费 4-注销服务费
     */
    @ApiModelProperty(value = "分润类型(订单类型)  1-会员升级费 2-工商注册（托管）费 3-开票服务费 4-注销服务费")
    private Integer profitsType;

    /**
     * 会员等级 0-税务顾问 1-城市服务商 2-城市合伙人 3-高级城市合伙人
     */
    @ApiModelProperty(value = "会员等级 0-税务顾问 1-城市服务商 2-城市合伙人 3-高级城市合伙人")
    private Integer levelNo;

}