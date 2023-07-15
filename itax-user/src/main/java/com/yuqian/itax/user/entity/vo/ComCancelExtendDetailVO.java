package com.yuqian.itax.user.entity.vo;

import com.github.pagehelper.PageInfo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @Description 推广记录（企业注销）明细展示VO
 * @Author  Kaven
 * @Date   2020/6/7 10:13 下午
*/
@Getter
@Setter
public class ComCancelExtendDetailVO implements Serializable {
    private static final long serialVersionUID = -1L;

    /**
     * 待付款
     */
    @ApiModelProperty(value = "待付款")
    private Long toBePayCount;

    /**
     * 注销处理中
     */
    @ApiModelProperty(value = "注销处理中")
    private Long toBeHandleCount;

    /**
     * 注销成功
     */
    @ApiModelProperty(value = "注销成功")
    private Long cancelSuccessCount;

    /**
     * 总注销服务费
     */
    @ApiModelProperty(value = "总注销服务费")
    private Long totalCancelFee;

    /**
     * 推广订单列表
     */
    @ApiModelProperty(value = "推广订单列表")
    private PageInfo orderPageList;
}
