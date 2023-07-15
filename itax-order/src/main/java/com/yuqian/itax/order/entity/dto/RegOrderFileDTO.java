package com.yuqian.itax.order.entity.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 *  @Author: Kaven
 *  @Date: 2019/12/11 19:13
 *  @Description: 工商注册订单文件上传接收DTO
 */
@Getter
@Setter
public class RegOrderFileDTO implements Serializable {
    private static final long serialVersionUID = -1L;

    @NotBlank(message = "文件路径不能为空")
    @ApiModelProperty(value = "文件路径",required = true)
    private String fileUrl;// 文件路径

    @NotBlank(message = "订单号不能为空")
    @ApiModelProperty(value = "订单号",required = true)
    private String orderNo;// 订单号

    @NotNull(message = "操作类型不能为空")
    @ApiModelProperty(value = "操作类型：1-更新签名图片 2-更新认证视频 3-补充资料上传",required = true)
    private Integer step;// 操作类型：1-更新签名图片 2-更新认证视频 3-补充资料上传

    @ApiModelProperty(value = "版本号",required = true)
    private String versionCode;// 版本号
}