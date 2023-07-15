package com.yuqian.itax.order.entity.query;

import com.yuqian.itax.common.base.entity.query.BaseQuery;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 *  @Description: 开票补传流水订单接收实体
 *  @Author: yejian
 *  @Date: 2020/07/20 15:09
 */
@Getter
@Setter
public class InvoiceWaterOrderApiQuery extends BaseQuery implements Serializable {

    /**
     * 订单号
     */
    @ApiModelProperty(value = "订单号")
    private String orderNo;

    /**
     * 公司名称（模糊查询）
     */
    @ApiModelProperty(value = "公司名称（模糊查询）")
    private String companyName;

    /**
     * 流水状态：0-待上传 1-审核中 2-审核通过 3-审核不通过
     */
    @ApiModelProperty(value = "流水状态：0-待上传 1-审核中 2-审核通过 3-审核不通过")
    private Integer bankWaterStatus;

    /**
     * 会员账号
     */
    @ApiModelProperty(value = "会员账号")
    private String regPhone;

}