package com.yuqian.itax.capital.entity.query;

import com.yuqian.itax.common.base.entity.query.BaseQuery;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UserCapitalChangeRecordQuery extends BaseQuery {
    private static final long serialVersionUID = -1L;

    /**
     * 明细类型 变动类型  1-收入 2-支出 3-冻结 4-解冻
     */
    private  Integer changesType;
    /**
     * 机构名称
     */
    private String oemName;
    /**
     * 明细描述
     */
    private  String detailDesc;

    /**
     * 订单类型 1-充值 2-代理充值 3-提现 4-代理提现 5 - 工商开户 6-开票 7-会费
     */
    private  Integer orderType;

    /**
     * 订单编号
     */
    private  String orderNo;

    /**
     * 查询账户得用户ID
     */
    private  Long userId;

    /**
     * 账号
     */
    private  String account;
    /**
     * 用户类型 1-会员 2 -系统用户
     */
    private  Integer userType;
    /**
     *钱包类型 1-消费钱包 2-佣金钱包
     */
    private Integer walletType;
    /**
     *账号编号
     */
    private String capitalAccount;

}
