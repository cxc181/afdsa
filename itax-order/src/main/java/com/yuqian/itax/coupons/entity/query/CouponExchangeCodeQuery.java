package com.yuqian.itax.coupons.entity.query;

import com.yuqian.itax.common.base.entity.query.BaseQuery;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CouponExchangeCodeQuery extends BaseQuery {


    Long id;
    /**
     * 兑换码名称
     */
    private String exchangeName;
    /**
     * 兑换码
     */
    private String exchangeCode;
    /**
     * 优惠券名称
     */
    private String couponsName;
    /**
     * 优惠券编码
     */
    private String couponsCode;
    /**
     * 状态 0-未生效 1-已生效 2-已过期 3-已作废 4-已暂停
     */
    private Integer status;
    /**
     * 面额最大
     */
    private Long faceAmountMax;
    /**
     * 面额最小
     */
    private Long faceAmountMin;
    /**
     * OEM机构
     */
    private String oemCode;


}
