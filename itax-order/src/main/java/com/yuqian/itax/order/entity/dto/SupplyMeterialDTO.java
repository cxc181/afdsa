package com.yuqian.itax.order.entity.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @Description 补充资料接收DTO
 * @Author  Kaven
 * @Date   2020/7/21 16:57
*/
@Getter
@Setter
public class SupplyMeterialDTO implements Serializable {
    private static final long serialVersionUID = -1L;

    @NotBlank(message = "文件路径不能为空")
    @ApiModelProperty(value = "文件地址，多个地址之间用 逗号 分割（图片，视频，单次最多12张",required = true)
    private String fileUrls;// 文件地址，多个地址之间用 逗号 分割（图片，视频，单次最多12张

    @NotBlank(message = "订单号不能为空")
    @ApiModelProperty(value = "订单号",required = true)
    private String orderNo;// 订单号

//    @NotBlank(message = "经营者手机号不能为空")
    @ApiModelProperty(value = "经营者手机号",required = true)
    private String regPhone;// 经营者手机号

    private String oemCode;// oem机构编码
}