package com.yuqian.itax.order.entity.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 账单明细收入和支出返回实体
 * @author：yejian
 * @Date：2020/03/05 09:40
 */
@Getter
@Setter
public class BillIncomePayVO implements Serializable {

    private static final long serialVersionUID = -1L;

    /**
     * 收入金额
     */
    @ApiModelProperty(value = "收入金额")
    private Long incomeAmount;

    /**
     * 支出金额
     */
    @ApiModelProperty(value = "支出金额")
    private Long payAmount;

}
