package com.yuqian.itax.pay.entity.query;

import com.yuqian.itax.common.base.entity.query.BaseQuery;
import lombok.Getter;
import lombok.Setter;


@Setter
@Getter
public class WthdrawQuery extends BaseQuery {
    private static final long serialVersionUID = -1L;

    /**
     * 订单编号
     */
    private  String orderNo;

    /**
     * 订单类型 1-充值 2-代理充值 3-提现 4-代理提现 5 - 工商开户 6-开票
     */
    private  Integer orderType;
    /**
     * 用户类型
     */
    private Integer userType;

    /**
     * 支付状态 0-待支付 1-支付中 2 -支付成功 3-支付失败
     */
    private  Integer payStatus;

    /**
     * 提现账号
     */
    private  String account;

    /**
     * 账户角色
     */
    private  String roleName;



    /**
     * 用户名称
     */
    private  String name;

    /**
     * 绑定手机
     */
    private  String phone;

    /**
     * OEM名称
     */
    private  String oemName;


    /**
     * OEM机构
     */
    private  String oemCode;





    /**
     * 流水类型 1-充值，2-提现 ，3-余额支付，4-第三方支付，5-退款
     */
    private Integer payWaterType;

    /**
     * 用户id()
     */
    private Long memberId;

    /**
     * 查询类型
     */
    private  Integer type;

    private String tree;

    /**
     * 钱包类型 1-消费钱包 2-佣金钱包
     */
    private Integer walletType;

    /**
     * 等级标识  0-普通会员 1-VIP 2-白银会员 3-税务顾问 4-铂金会员 5-城市服务商
     */
    private Integer levelNo;
    /**
     * 完成开始时间
     */
    private String startCompleteDate;

    /**
     * 完成结束时间
     */
    private String endCompleteDate;
}
