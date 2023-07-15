package com.yuqian.itax.order.entity.query;

import com.yuqian.itax.common.base.entity.query.BaseQuery;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 账单明细查询实体
 * @author：yejian
 * @Date：2019/12/23 14:52
 * @version：1.0
 */
@Getter
@Setter
public class BillDetailQuery extends BaseQuery implements Serializable {

    private static final long serialVersionUID = -1L;

    /**
     * 钱包类型 1-消费钱包 2-佣金钱包
     */
    @NotNull(message="钱包类型不能为空")
    @Min(value = 1, message = "钱包类型有误")
    @Max(value = 2, message = "钱包类型有误")
    @ApiModelProperty(value = "钱包类型 1-消费钱包 2-佣金钱包", required = true)
    private Integer walletType;

    /**
     * 查询类型：1->全部；2->充值；3->提现；4->推广分润；5->企业注册；6->企业开票；7->会员升级；8->企业注销；9->证件领用申请；10->对公户申请
     */
    @NotNull(message="查询类型不能为空")
    @Min(value = 1, message = "查询类型有误")
    @Max(value = 15, message = "查询类型有误")
    @ApiModelProperty(value = "查询类型：1->全部；2->充值；3->提现；4->推广分润；5->企业注册；6->企业开票；7->会员升级；8->企业注销；9->证件领用申请；10->对公户申请", required = true)
    private Integer type;

    /**
     * 月份
     */
    @ApiModelProperty(value = "月份")
    private String month;

}
