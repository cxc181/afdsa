package com.yuqian.itax.coupons.entity.vo;

import lombok.Getter;
import lombok.Setter;
import org.jeecgframework.poi.excel.annotation.Excel;

import java.io.Serializable;

@Setter
@Getter
public class CouponsBatchIssueVO implements Serializable {
    private static final long serialVersionUID = -1L;
    /**
     * 优惠券编码
     */
    @Excel(name = "优惠券编码")
    private String couponsCode;
    /**
     * 发放账号
     */
    @Excel(name = "发放账号")
    private String memberAccount;
    /**
     * 张数
     */
    @Excel(name = "张数")
    private Integer number;

    /**
     * 失败原因
     */
    @Excel(name = "失败原因")
    private String failedMsg;
}
