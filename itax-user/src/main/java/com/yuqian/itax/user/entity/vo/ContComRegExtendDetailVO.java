package com.yuqian.itax.user.entity.vo;

import com.github.pagehelper.PageInfo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @Description 推广记录明细（托管费续费）展示VO
 * @Author  HZ
 * @Date   2021/4/6 10:13 下午
 */
@Getter
@Setter
public class ContComRegExtendDetailVO implements Serializable {

    private static final long serialVersionUID = -1L;
    /**
     * 总托管费
     */
    @ApiModelProperty(value = "总托管费")
    private Long totalServiceFee;


    /**
     * 推广订单列表
     */
    @ApiModelProperty(value = "推广订单列表")
    private PageInfo orderPageList;
}
