package com.yuqian.itax.order.entity.vo;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 开票订单变更记录
 * 
 * @Date: 2019年12月07日 19:54:14 
 * @author 蒋匿
 */
@Getter
@Setter
public class InvoiceOrderChangeRecordVO implements Serializable {
	
	private static final long serialVersionUID = -1L;
	
	/**
	 * 主键id
	 */
	private Long id;
	
	/**
	 * 订单号
	 */
	private String orderNo;
	
	/**
	 * 机构编码
	 */
	private String oemCode;
	
	/**
	 * 订单状态 0-待创建 1-待付款 2-待审核 3-出票中 4-待发货 5-出库中 6-待收货 7-已签收  8-已取消
	 */
	private Integer orderStatus;
	
	/**
	 * 企业id
	 */
	private Long companyId;
	
	/**
	 * 开票金额
	 */
	private Long invoiceAmount;
	
	/**
	 * 增值税费
	 */
	private Long vatFee;

	/**
	 * 增值税税率
	 */
	private BigDecimal vatFeeRate;

	/**
	 * 增值税补缴
	 */
	private Long vatPayment;

	/**
	 * 增值税计税金额
	 */
	private Long vatFeeQuota;
	
	/**
	 * 个人所得税
	 */
	private Long personalIncomeTax;

	/**
	 * 个人所得税税率
	 */
	private BigDecimal personalIncomeTaxRate;

	/**
	 * 个人所得税补缴
	 */
	private Long incomeTaxPayment;

	/**
	 * 个人所得税计税金额
	 */
	private Long personalIncomeTaxQuota;

	/**
	 * 附加税
	 */
	private Long surcharge;

	/**
	 * 附加税税率
	 */
	private BigDecimal surchargeRate;

	/**
	 * 附加税补缴
	 */
	private Long surchargePayment;

	/**
	 * 服务费
	 */
	private Long serviceFee;
	
	/**
	 * 服务费优惠
	 */
	private Long serviceFeeDiscount;
	
	/**
	 * 发票抬头公司名称
	 */
	private String companyName;
	
	/**
	 * 发票抬头公司地址
	 */
	private String companyAddress;
	
	/**
	 * 发票抬头税号
	 */
	private String ein;
	
	/**
	 * 发票抬头电话号码
	 */
	private String phone;
	
	/**
	 * 发票抬头注册地址
	 */
	private String registAddress;
	
	/**
	 * 发票抬头开户银行
	 */
	private String bankName;
	
	/**
	 * 发票抬头银行账号
	 */
	private String bankNumber;
	
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
	 * 开票类目id
	 */
	private Long categoryId;
	
	/**
	 * 开票类目名称
	 */
	private String categoryName;
	
	/**
	 * 支付金额
	 */
	private Long payAmount;
	
	/**
	 * 发票类型 1-普通发票 2-增值税发票
	 */
	private Integer invoiceType;
	
	/**
	 * 开票类型名称
	 */
	private String invoiceTypeName;
	
	/**
	 * 银行流水截图
	 */
	private String accountStatement;
	
	/**
	 * 邮寄费金额
	 */
	private Long postageFees;
	
	/**
	 * 添加时间
	 */
	private Date addTime;
	
	/**
	 * 添加人
	 */
	private String addUser;
	
	/**
	 * 修改时间
	 */
	private Date updateTime;
	
	/**
	 * 修改人
	 */
	private String updateUser;
	
	/**
	 * 备注
	 */
	private String remark;

	/**
	 * 通知次数
	 */
	private String alertNumber;

	/**
	 * 发票方式
	 */
	private Integer invoiceWay;

	/**
	 * 抬头省编码
	 */
	private String provinceCode;

	/**
	 * 抬头省名称
	 */
	private String provinceName;

	/**
	 * 抬头市编码
	 */
	private String cityCode;

	/**
	 * 抬头市名称
	 */
	private String cityName;

	/**
	 * 抬头区编码
	 */
	private String districtCode;

	/**
	 * 抬头区名称
	 */
	private String districtName;

	/**
	 * 发票图片地址，多个图片之间用逗号分割
	 */
	private String invoiceImgs;

	/**
	 * 是否先开票后补流水 0-先开票后补流水 1-先上传流水再开票
	 */
	private Integer isAfterUploadBankWater;

	/**
	 * 流水状态 0-待上传 1-审核中 2-审核通过 3-审核不通过 4-流水前置
	 */
	private Integer bankWaterStatus;

	/**
	 * '审核失败原因
	 */
	private String auditErrorRemark;

	/**
	 * 签收时间
	 */
	private Date completeTime;

	/**
	 * 商品名称
	 */
	private String goodsName;

	/**
	 * 钱包类型 1-消费钱包 2-佣金钱包
     */
    private Integer walletType;

    /**
     * 开票方式 1-自助开票 2-集团代开 3-佣金开票
     */
    private Integer createWay;

    /**
     * 业务合同
     */
    private String businessContractImgs;

	/**
	 * 收款凭证
	 */
	private String receiptPaymentVoucher;

	/**
	 * 图片地址
	 */
	private List<String> imgUrl;

}
