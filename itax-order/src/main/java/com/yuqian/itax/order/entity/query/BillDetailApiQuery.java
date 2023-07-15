package com.yuqian.itax.order.entity.query;

import com.yuqian.itax.common.base.entity.query.BaseQuery;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 账单明细查询实体
 *
 * @author：yejian
 * @Date：2019/12/23 14:52
 * @version：1.0
 */
@Getter
@Setter
public class BillDetailApiQuery extends BaseQuery implements Serializable {

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
     * 查询类型：1->全部；3->提现；4->推广分润
     */
    @NotNull(message = "查询类型不能为空")
    @ApiModelProperty(value = "查询类型：1->全部；3->提现；4->推广分润", required = true)
    private Integer type;

    /**
     * 月份
     */
    @ApiModelProperty(value = "月份")
    private String month;

}
