package com.yuqian.itax.order.entity.query;

import com.yuqian.itax.common.base.entity.query.BaseQuery;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 *  @Author: yejian
 *  @Date: 2020/03/03 15:19
 *  @Description: 开票订单查询实体类
 */
@Getter
@Setter
public class InvOrderQuery extends BaseQuery implements Serializable {

    /**
     * 查询类型：1->全部；2->审核中；3->待发货；4->已发货；5->已签收
     */
    @ApiModelProperty(value = "查询类型：1->全部；2->审核中；3->待发货；4->已发货；5->已签收",required = true)
    private Long type;

    /**
     * 企业ID
     */
    @ApiModelProperty(value = "企业ID",required = true)
    private Long companyId;

    /**
     * 月份
     */
    @ApiModelProperty(value = "月份",required = true)
    private String month;

}