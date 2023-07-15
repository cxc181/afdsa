package com.yuqian.itax.order.entity.vo;

import com.github.pagehelper.PageInfo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 账单记录返回实体
 *
 * @author：yejian
 * @Date：2020/11/17 10:52
 */
@Getter
@Setter
public class BillRecordVO implements Serializable {

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

    /**
     * 账单明细（分页）
     */
    @ApiModelProperty(value = "账单明细（分页）")
    private PageInfo<BillDetailVO> billPageData;

}
