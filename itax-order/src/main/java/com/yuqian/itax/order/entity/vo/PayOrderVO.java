package com.yuqian.itax.order.entity.vo;

import com.yuqian.itax.order.entity.InvoiceOrderEntity;
import com.yuqian.itax.order.entity.OrderEntity;
import com.yuqian.itax.pay.entity.vo.PaywaterVO;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 支付信息实体
 * @author：pengwei
 * @Date：2020/3/4 13:52
 * @version：1.0
 */
@Setter
@Getter
public class PayOrderVO {

    /**
     * 支付流水号
     */
    private String payNo;

    /**
     * 支付方式  1-微信 2-余额 3-支付宝 4-快捷支付
     */
    private Integer payWay;

    /**
     * 订单金额
     */
    private Long orderAmount;

    /**
     * 优惠金额
     */
    private Long discountAmount;

    /**
     * 支付金额
     */
    private Long payAmount;

    /**
     * 服务费
     */
    private Long serviceFee;

    /**
     * 增值税费
     */
    private Long vatFee;

    /**
     * 个人所得税
     */
    private Long personalIncomeTax;

    /**
     * 邮寄费金额
     */
    private Long postageFees;

    /**
     * 增值税补缴
     */
    private Long vatPayment;

    /**
     * 附加税
     */
    private Long surcharge;

    /**
     * 附加税补缴
     */
    private Long surchargePayment;

    /**
     * 优惠券金额
     */
    private String faceAmount;

    /**
     * 所得税补缴
     */
    private Long incomeTaxPayment;

    /**
     * 产品特价id
     */
    private Long discountActivityId;

    /**
     * 打款凭证
     */
    private String payPic;

    /**
     * 打款凭证图片列表
     */
    private List<String> payPicList;

    public PayOrderVO() {

    }
    public PayOrderVO(OrderEntity orderEntity, PaywaterVO paywaterVO) {
        if (paywaterVO != null) {
            this.payNo = paywaterVO.getPayNo();
            this.payWay = paywaterVO.getPayWay();
            this.payPic = paywaterVO.getPayPic();
        }
        this.orderAmount = orderEntity.getOrderAmount();
        this.discountAmount = orderEntity.getDiscountAmount();
        this.payAmount = orderEntity.getPayAmount();
    }

    public PayOrderVO(OrderEntity orderEntity, PaywaterVO paywaterVO, InvoiceOrderEntity invoiceOrderEntity) {
        if (paywaterVO != null) {
            this.payNo = paywaterVO.getPayNo();
            this.payWay = paywaterVO.getPayWay();
        }
        this.discountAmount = orderEntity.getDiscountAmount();
        this.payAmount = orderEntity.getPayAmount();
        this.serviceFee = invoiceOrderEntity.getServiceFee();
        this.vatFee = invoiceOrderEntity.getVatFee();
        this.personalIncomeTax = invoiceOrderEntity.getPersonalIncomeTax();
        this.postageFees = invoiceOrderEntity.getPostageFees();
        this.vatPayment = invoiceOrderEntity.getVatPayment();
        this.surcharge = invoiceOrderEntity.getSurcharge();
        this.surchargePayment = invoiceOrderEntity.getSurchargePayment();
        this.incomeTaxPayment = invoiceOrderEntity.getIncomeTaxPayment();
        this.payPic = paywaterVO.getPayPic();

    }

}