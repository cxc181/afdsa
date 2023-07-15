package com.yuqian.itax.order.entity.vo;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Setter
@Getter
public class ThirdPartyQueryInoiveInfoVO implements Serializable {
    private static final long serialVersionUID = -1L;
    /**
     * 订单编号
     */
    private String orderNo;
    /**
     * 订单状态
     */
    private Integer orderStatus;
    /**
     * 企业名称
     */
    private String memberCompanyName;
    /**
     * 开票金额
     */
    private Long invoiceAmount;
    /**
     * 支付金额
     */
    private Long payAmount;
    /**
     * 个人所得税
     */
    private Long personalIncomeTax;
    /**
     * 增值税
     */
    private Long vatFee;
    /**
     * 附加税
     */
    private Long surcharge;
    /**
     * 服务费
     */
    private Long serviceFee;
    /**
     * 增值税率
     */
    private BigDecimal vatFeeRate;
    /**
     * 发票类型 1、电子发票；2、纸质发票
     */
    private Integer invoiceWay;
    /**
     * 开票类型 1、增值税普通发票；2、增值税专用发票
     */
    private Integer invoiceType;
    /**
     * 开票类目
     */
    private String  categoryName;
    /**
     * 抬头公司名称
     */
    private String companyName;
    /**
     * 抬头公司地址
     */
    private String companyAddress;
    /**
     * 抬头税号
     */
    private String ein;
    /**
     * 银行卡号
     */
    private String bankNumber;

    /**
     * 开户行
     */
    private String bankName;
    /**
     * 抬头电话
     */
    private String phone;
    /**
     * 发票抬头收件人
     */
    private String recipient;
    /**
     * 发票抬头联系电话
     */
    private String recipientPhone;

    /**
     * 发票抬头详细地址
     */
    private String recipientAddress;
    /**
     * 发票抬头省编码
     */
    private String provinceCode;

    /**
     * 发票抬头省名称
     */
    private String provinceName;

    /**
     * 发票抬头市编码
     */
    private String cityCode;

    /**
     * 发票抬头市名称
     */
    private String cityName;

    /**
     * 发票抬头区编码
     */
    private String districtCode;

    /**
     * 发票抬头区名称
     */
    private String districtName;

    /**
     * 邮箱
     */
    private String email;
    /**
     * 发票备注
     */
    private String invoiceRemark;
    /**
     * 订单备注
     */
    private String remark;
    /**
     * 经营者姓名
     */
    private String operatorName;
    /**
     * 园区名称
     */
    private String parkName;
    /**
     * 发票图片,地址字符串
     */
    private String invoiceImgs;
    /**
     * 发票图片列表，base64
     */
    private List<String> invImgList;
    /**
     * 快递单号
     */
    private String courierNumber;
    /**
     * 快递公司
     */
    private String courierCompanyName;
    /**
     * 商品明细
     */
    private List<GoodsDetailVO> goodsDetails;
    /**
     * 出票时间
     */
    private String confirmInvoiceTime;
    /**
     * 审核失败原因
     */
    private String auditFailReason;
}
