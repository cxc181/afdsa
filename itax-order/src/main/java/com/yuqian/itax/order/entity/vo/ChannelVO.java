package com.yuqian.itax.order.entity.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 *  @Author: wangkail
 *  @Date: 2021/5/8 15:40
 *  @Description: 渠道来源展示
 */
@Getter
@Setter
public class ChannelVO implements Serializable {

    private static final long serialVersionUID = -1L;

    /**
     * 主键id
     */
    @ApiModelProperty(value = "主键id")
    private Long id;

    /**
     * 渠道名称
     */
    @ApiModelProperty(value = "渠道名称")
    private String channelName;

    /**
     * 渠道编码
     */
    @ApiModelProperty(value = "渠道编码")
    private String channelCode;

}
