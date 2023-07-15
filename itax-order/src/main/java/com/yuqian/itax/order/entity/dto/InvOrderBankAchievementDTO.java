package com.yuqian.itax.order.entity.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 *  @Description: 开票订单补传流水DTO
 *  @Author: yejian
 *  @Date: 2020/05/18 09:29
 */
@Getter
@Setter
public class InvOrderBankAchievementDTO implements Serializable {

    private static final long serialVersionUID = -1L;

    /**
     * 订单号
     */
    @NotBlank(message="订单号不能为空")
    @ApiModelProperty(value = "订单号",required = true)
    private String orderNo;

    /**
     *
     */
//    @NotBlank(message = "会员账号不能为空")
    @ApiModelProperty(value = "会员账号",required = true)
    private String regPhone;

    /**
     * 成果图片，多个图片之间用逗号分割
     */
    @NotBlank(message="成果截图不能为空")
    @ApiModelProperty(value = "成果图片（多张逗号拼接）")
    private String achievementImgs;
    /**
     * 成果视频
     */
    @ApiModelProperty(value = "成果视频")
    private String achievementVideo;

}