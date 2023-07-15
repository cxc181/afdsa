package com.yuqian.itax.profits.entity.query;

import com.yuqian.itax.common.base.entity.query.BaseQuery;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @Author: Kaven
 * @Date: 2019/12/20 9:28
 * @Description: 会员分润记录查询bean
 */
@Getter
@Setter
public class MemberProfitsApiQuery extends BaseQuery implements Serializable {
    private static final long serialVersionUID = -1L;

    /**
     * 会员ID
     */
    @NotNull(message = "会员ID不能为空")
    @ApiModelProperty(value = "会员ID", required = true)
    private Long userId;

    /**
     * 会员等级 0-税务顾问 1-城市服务商 2-城市合伙人 3-高级城市合伙人
     */
    @NotNull(message = "会员等级不能为空")
    @ApiModelProperty(value = "会员等级 0-税务顾问 1-城市服务商 2-城市合伙人 3-高级城市合伙人", required = true)
    private Integer levelNo;

    /**
     * 月份
     */
    @ApiModelProperty(value = "月份")
    private String month;

    /**
     * 开始时间
     */
    @ApiModelProperty(value = "开始时间")
    private String startDate;

    /**
     * 结束时间
     */
    @ApiModelProperty(value = "结束时间")
    private String endDate;

    /**
     * 分润类型(订单类型)  1-会员升级费 2-工商注册（托管）费 3-开票服务费 4-注销服务费
     */
    @ApiModelProperty(value = "分润类型(订单类型)  1-会员升级费 2-工商注册（托管）费 3-开票服务费 4-注销服务费")
    private Integer profitsType;

}