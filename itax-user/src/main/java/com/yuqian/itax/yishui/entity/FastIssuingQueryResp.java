package com.yuqian.itax.yishui.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * @author Administrator
 */
@Data
public class FastIssuingQueryResp extends YiShuiBaseResp implements Serializable {

    /**
     * 订单ID
     */
    private Long enterprise_order_ext_id;

    /**
     * 订单编号
     */
    private String enterprise_order_ext_sn;

    /**
     * 订单批次ID
     */
    private Long enterprise_order_id;

    /**
     * 人员编号，同新增人员返回的“professional_sn”
     */
    private String client_sn;

    /**
     * 人员姓名
     */
    private String client_name;

    /**
     * 类型
     */
    private Integer client_type;

    /**
     * 付款金额
     */
    private String real_money;

    /**
     * 实际出款金额
     */
    private String plat_money;

    /**
     * 服务费费率
     */
    private String process_rate;
    /**
     * 服务费
     */
    private String process_commission;
    /**
     * 银行卡号
     */
    private String process_diff;
    /**
     * 差额
     */
    private Integer op_type;
    /**
     * 支付单号
     */
    private String pay_sn;

    /**
     * 支付时间
     */
    private Long pay_time;

    /**
     * 支付渠道ID
     */
    private Long pay_type;
    /**
     * 批次状态
     */
    private Integer status;

    /**
     * 支付状态 0 待审核 1 打款中 2打款失败 3拒绝打款 4打款成功
     */
    private Integer payment_status;

    /**
     * 支付失败 银行返回的失败原因
     */
    private String pay_err_msg;

    /**
     * 业绩说明
     */
    private String remark;

    /**
     * 收款卡号
     */
    private String bank_code;

    /**
     *  扣费模式 0内扣 1外扣
     */
    private Integer deduction_mode;

    /**
     * 银行预留手机号码
     */
    private String mobile;

    /**
     * 项目ID
     */
    private Long crowd_id;

    /**
     * 第三方单号
     */
    private String request_no;
}
