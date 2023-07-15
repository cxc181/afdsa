package com.yuqian.itax.coupons.entity.po;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.math.BigDecimal;

@Setter
@Getter
public class CouponPO implements Serializable {
    private static final long serialVersionUID = -1L;


    private Long id ;
    /**
     * OEM机构
     */
    @NotBlank(message = "请选择OEM机构" )
    private String oemCode;
    /**
     * 优惠券名称
     */
    @NotBlank(message = "请输入优惠券名称" )
    @Size(min = 0, max = 10, message = "优惠券名称最多10个字")
    private String couponsName;
    /**
     * 面额
     */
    @NotNull(message = "请输入优惠券面额" )
    private BigDecimal faceAmount;
    /**
     *有效期开始
     */
    @NotBlank(message = "请选择优惠券有效期" )
    private String startDate;
    /**
     *有效期结束
     */
    @NotBlank(message = "请选择优惠券有效期" )
    private String endDate;
    /**
     *可用范围，多个之间用户逗号分割  1-个体户注册 2-开票 3-注销 4-个体户续费
     */
    @NotBlank(message = "请选择优惠券可用范围" )
    private String usableRange;

    /**
     *描述
     */
    private String description;

    /**
     *状态 0-未生效 1-已生效 2-已过期 3-已作废 4-已暂停
     */
    private Integer status;
}
