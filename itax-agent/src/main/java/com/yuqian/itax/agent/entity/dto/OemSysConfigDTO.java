package com.yuqian.itax.agent.entity.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * OEM机构系统配置
 * @author：pengwei
 * @Date：2020/3/5 9:12
 * @version：1.0
 */
@Setter
@Getter
public class OemSysConfigDTO implements Serializable {

    private static final long serialVersionUID = -1L;

    /**
     *  OEM机构主键id
     */
    @NotNull(message="id不能为空")
    private  Long id;

    /**
     * 是否邀请人校验  0-不校验 1-校验
     */
    @NotNull(message="是否校验邀请人存在不能为空")
    @Min(value = 0, message = "是否校验邀请人存在状态有误")
    @Max(value = 1, message = "是否校验邀请人存在状态有误")
    private Integer isInviterCheck;

    /**
     * 是否开通推广中心 0-否 1-是
     */
    @NotNull(message="是否开通推广中心不能为空")
    @Min(value = 0, message = "是否开通推广中心状态有误")
    @Max(value = 1, message = "是否开通推广中心状态有误")
    private Integer isOpenPromotion;

    /**
     * 邀请员工上限
     */
//    @NotNull(message="邀请员工上限只能为0~999999")
//    @Min(value = 0, message = "邀请员工上限只能为0~999999")
//    @Max(value = 999999, message = "邀请员工上限只能为0~999999")
//    private Integer employeesLimit;
    /**
     * 城市服务商以下提现手续费率
     */
    @NotNull(message="城市服务商以下提现手续费率上限只能为0~99")
    @Min(value = 0, message = "城市服务商以下提现手续费率上限只能为0~99")
    @Max(value = 99, message = "城市服务商以下提现手续费率上限只能为0~99")
    private BigDecimal commissionServiceFeeRate;
    /**
     * 城市服务商提现手续费率
     */
//    @NotNull(message="城市服务商提现手续费率上限只能为0~99")
//    @Min(value = 0, message = "城市服务商提现手续费率上限只能为0~99")
//    @Max(value = 99, message = "城市服务商提现手续费率上限只能为0~99")
//    private BigDecimal diamondCommissionServiceFeeRate;

    /**
     *  是否接入国金助手 1-接入 0-不接入
      */
    @NotNull(message="是否接入国金助手")
    @Min(value = 0, message = "是否接入国金助手参数错误")
    @Max(value = 1, message = "是否接入国金助手参数错误")
    private Integer isOpenChannel;

    /**
     *  国金助手地址
     */
    private String channelUrl;

    /**
     *  国金加密参数
     */
    private String secKey;

    /**
     * 工单审核方 1-平台客服  2-oem机构客服
     */
    private Integer workAuditWay;
}
