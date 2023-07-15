package com.yuqian.itax.coupons.entity.po;

import com.yuqian.itax.util.validator.Add;
import com.yuqian.itax.util.validator.Update;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;

@Setter
@Getter
public class CouponExchangeCodePO implements Serializable {
    private static final long serialVersionUID = -1L;

    @NotNull(message = "请输入兑换码id" ,groups={Update.class})
    private Long id ;
    /**
     * 兑换码名称
     */
    @NotBlank(message = "请输入兑换码名称"  ,groups={Add.class})
    @Size(min = 0, max = 20, message = "兑换码名称最多20个字")
    private String exchangeName;
    /**
     * 优惠卷编码
     */
    @NotNull(message = "请输入优惠卷编码" ,groups={Add.class})
    private String couponsCode;
    /**
     *有效期开始
     */
    @NotBlank(message = "请选择兑换码有效期" ,groups={Add.class,Update.class})
    private String startDate;
    /**
     *有效期结束
     */
    @NotBlank(message = "请选择兑换码有效期"  ,groups={Add.class,Update.class})
    private String endDate;
    /**
     * 限量兑换张数
     */
    @NotNull(message = "请输入限量兑换张数" )
    @Min(value = 1, message = "限量兑换张数只支持1~10000", groups={Add.class, Update.class})
    @Max(value = 10000, message = "限量兑换张数只支持1~10000", groups={Add.class, Update.class})
    private Integer limitedNumber;
    /**
     * 每人可兑换张数
     */
    @NotNull(message = "请输入每人可兑换张数" )
    @Min(value = 1, message = "每人可兑换张数只支持1~10000", groups={Add.class, Update.class})
    @Max(value = 10000, message = "每人可兑换张数只支持1~10000", groups={Add.class, Update.class})
    private Integer exchangeNumberPerson;
    /**
     *描述
     */
    private String remark;

    /**
     *状态 0-未生效 1-已生效 2-已过期 3-已作废 4-已暂停
     */
    private Integer status;
}
