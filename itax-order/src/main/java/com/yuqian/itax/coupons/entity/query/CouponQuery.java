package com.yuqian.itax.coupons.entity.query;

import com.yuqian.itax.common.base.entity.query.BaseQuery;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CouponQuery extends BaseQuery {


    Long id;
    /**CouponsController
     * 优惠券名称
     */
    private String couponsName;
    /**
     * 优惠券编码
     */
    private String couponsCode;
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
    /**
     * 状态 0-未生效 1-已生效 2-已过期 3-已作废 4-已暂停
     */
    private Integer status;

    /**
     *  可用范围  1-个体注册 2-个独注册 3-有限合伙注册 4-有限责任注册
     */
    private Integer usableRange;

}
