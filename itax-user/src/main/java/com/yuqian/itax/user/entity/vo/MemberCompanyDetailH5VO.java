package com.yuqian.itax.user.entity.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
@Setter
@Getter
public class MemberCompanyDetailH5VO implements Serializable {

    private static final long serialVersionUID = -1L;

    /**
     * 用户ID
     */
    @ApiModelProperty(value = "经营者名称")
    private Long memberId;
    /**
     * 经营者名称
     */
    @ApiModelProperty(value = "经营者名称")
    private String operatorName;

    /**
     * 经营者身份证号码
     */
    @ApiModelProperty(value = "经营者身份证号码")
    private String idCardNumber;

    /**
     * 经营者手机号
     */
    @ApiModelProperty(value = "经营者手机号")
    private String operatorTel;

    /**
     * 订单号
     */
    @ApiModelProperty(value = "订单号")
    private String orderNo;

    /**
     * 签名
     */
    @ApiModelProperty(value = "签名")
    private String sign;


    /**
     * 签名时间
     */
    @ApiModelProperty(value = "签名时间")
    private String signTime;
}
