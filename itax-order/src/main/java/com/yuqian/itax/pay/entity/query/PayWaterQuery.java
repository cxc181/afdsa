package com.yuqian.itax.pay.entity.query;

import com.yuqian.itax.common.base.entity.query.BaseQuery;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class PayWaterQuery extends BaseQuery {
    private static final long serialVersionUID = -1L;

    /**
     * 支付流水
     */
    private  String payNo;
    /**
     * 流水类型 1-充值，2-提现 ，3-余额支付，4-第三方支付，5-退款 6-对公户提现 7-企业退税 8-线下支付
     */
    private Integer payWaterType;
    /**
     * 外部流水号
     */
    private  String externalOrderNo;
    /**
     * 用户姓名
     */
    private String realName;

    /**
     * 用户手机号
     */
    private String memberPhone;
    /**
     * 订单编号
     */
    private  String orderNo;
    /**
     * 订单类型 1-充值 2-代理充值 3-提现 4-代理提现 5 - 工商注册 6-开票 7-用户升级 8-工商注销 9-证件申请 10-对公户申请 11-对公户提现 12-消费开票 13-补税 14-退税 15-托管费续费
     */
    private  Integer orderType;

    /**
     * 钱包类型 1-消费钱包 2-佣金钱包
     */
    private  Integer walletType;


    /**
     * 支付方式  1-微信 2-余额 3-支付宝 4-快捷支付  5-字节跳动 6-线下转账
     */
    private  Integer payWay;
    /**
     * 支付通道  1-微信 2-余额 3-支付宝 4-易宝支付 5-建行 6-北京代付 7-纳呗 8-字节跳动支付 9-线下
     */
    private  Integer payChannels;
    /**
     * 支付账号
     */
    private  String payAccount;

    /**
     * 银行名称
     */
    private  String payBank;

    /**
     * 所属机构
     */
    private  String oemName;




    private String tree;

    /**
     * 排除流水类型 1-充值，2-提现 ，3-余额支付，4-第三方支付，5-退款
     */
    private Integer notPayWaterType;

    /**
     * 支付状态 0-待支付 1-支付中 2 -支付成功 3-支付失败 4-待财务审核 5-财务审核失败 6-待财务打款（线下）
     */
    private Integer payStatus;
    /**
     * 完成开始时间
     */
    private String startCompleteDate;

    /**
     * 完成结束时间
     */
    private String endCompleteDate;

    /**
     * 退款状态 1-退款中 2-退款成功 3-退款失败
     */
    private Integer refundStatus;


    /**
     * 收单oem机构名称
     */
    private String otherPayOemName;

    /**
     * 收单机构oemcode
     */
    private String otherPayOemcode;
}
