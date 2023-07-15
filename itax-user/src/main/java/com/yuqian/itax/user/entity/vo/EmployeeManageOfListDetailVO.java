package com.yuqian.itax.user.entity.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class EmployeeManageOfListDetailVO implements Serializable {

    private static final long serialVersionUID = -1L;

    /**
     * 直推用户数
     */
    @ApiModelProperty(value = "直推用户数")
    private Long pushMemberCount = 0L;

    /**
     * 直推个体数
     */
    @ApiModelProperty(value = "直推个体数")
    private Long pushPersonalityCount = 0L;

    /**
     * 直推托管费
     */
    @ApiModelProperty(value = "直推托管费")
    private Long pushComRegFeeDirect = 0L;

    /**
     * 直推开票服务费
     */
    @ApiModelProperty(value = "直推开票服务费")
    private Long pushInvoiceFeeDirect = 0L;

    /**
     * 直推注销服务费
     */
    @ApiModelProperty(value = "直推注销服务费")
    private Long pushComCancelFeeDirect = 0L;

    /**
     * 直推会员升级费
     */
    @ApiModelProperty(value = "直推会员升级费")
    private Long pushMemberUpgradeFeeDirect = 0L;

    /**
     * 直推托管费续费
     */
    @ApiModelProperty(value = "直推托管费续费")
    private Long pushContRegister = 0L;

    /**
     * 裂变托管费续费
     */
    @ApiModelProperty(value = "裂变托管费续费")
    private Long fissionContRegister = 0L;

    /**
     * 裂变用户数
     */
    @ApiModelProperty(value = "裂变用户数")
    private Long fissionMemberCount = 0L;

    /**
     * 裂变个体数
     */
    @ApiModelProperty(value = "裂变个体数")
    private Long fissionPersonalityCount = 0L;

    /**
     * 裂变托管费
     */
    @ApiModelProperty(value = "裂变托管费")
    private Long fissionComRegFeeFission = 0L;

    /**
     * 裂变开票服务费
     */
    @ApiModelProperty(value = "裂变开票服务费")
    private Long fissionInvoiceFeeFission = 0L;

    /**
     * 裂变注销服务费
     */
    @ApiModelProperty(value = "裂变注销服务费")
    private Long fissionComCancelFeeFission = 0L;

    /**
     * 累计分润费
     */
    @ApiModelProperty(value = "累计分润费")
    private Long totalProfit = 0L;

}
