package com.yuqian.itax.user.entity.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 *  @Author: Kaven
 *  @Date: 2019/12/18 18:39
 *  @Description: 城市服务商统计接收dto
 */
@Getter
@Setter
public class MemberExtendDTO  implements Serializable {
    private static final long serialVersionUID = -1L;

    /**
     * 等级号：1-一级推广记录 2-二级推广记录
     */
    @ApiModelProperty(value = "等级号：1-一级推广记录 2-二级推广记录，默认查1级推广记录，当会员类型为城市服务商时必传")
    private Integer gradeNo;

    /**
     * 订单状态
     */
    @ApiModelProperty(value = "订单状态，默认查审核中的记录")
    private Integer orderStatus;

    @ApiModelProperty(value = "会员类型：-1-员工 1-税务顾问 2-城市服务商",required = true)
    private Integer memberType;
}