package com.yuqian.itax.profits.entity.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
public class MemberProfitsVO implements Serializable {

    private static final long serialVersionUID = -1L;

    /**
     * 订单类型
     */
    @ApiModelProperty(value = "订单类型  1-会员升级 2-工商注册 3-开票 4-企业注销 5-会费返还")
    private Integer orderType;

    /**
     * 分润标题
     */
    @ApiModelProperty(value = "分润标题")
    private String profitsName;

    /**
     * 推广等级
     */
    @ApiModelProperty(value = "推广等级")
    private String level;

    /**
     * 用户姓名
     */
    @ApiModelProperty(value = "用户姓名")
    private String realName;

    /**
     * 员工姓名
     */
    @ApiModelProperty(value = "员工姓名")
    private String staffName;

    /**
     * 分润率
     */
    @ApiModelProperty(value = "分润率")
    private BigDecimal profitsRate;

    /**
     * 分润金额
     */
    @ApiModelProperty(value = "分润金额")
    private Long profitsAmount = 0L;

    /**
     * 订单金额
     */
    @ApiModelProperty(value = "订单金额")
    private Long orderAmount = 0L;

    /**
     * 分润时间
     */
    @ApiModelProperty(value = "分润时间")
    private Date profitsTime;

    /**
     * 备注
     */
    @ApiModelProperty(value = "备注")
    private String remark;
}
