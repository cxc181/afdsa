package com.yuqian.itax.profits.entity.query;

import com.yuqian.itax.common.base.entity.query.BaseQuery;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ProfitsDetailQuery extends BaseQuery {
    private static final long serialVersionUID = -1L;

    /**
     * 订单编号
     */
    private String orderNo;
    /**
     * 订单类型  1-会员升级 2-工商开户 3-开票 4-工商注销 6-托管费续费
     */
    private String orderType;
    /**
     * 分润类型  1-会费 2-托管费 3-开票服务费 4-注销服务费 5-会费返还 6-托管费续费
     */
    private String profitsType;
    /**
     * 分润流水号
     */
    private String profitsNo;
    /**
     * 出款方
     */
    private String oemName;
    /**
     * 分润状态
     */
    private String profitsStatus;
    /**
     * 入款方
     */
    private String userName;
    /**
     * 入款方账号
     */
    private String userAcount;

    /**
     * 用户ID
     */
    private Long userId;
    /**
     * 查询类型
     */
    private Integer type;

    /**
     * 用户类型
     */
    private  Integer userType;


    private String tree;


}
