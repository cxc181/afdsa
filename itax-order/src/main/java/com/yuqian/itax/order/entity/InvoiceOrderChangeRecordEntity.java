package com.yuqian.itax.order.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
/**
 * 开票订单变更记录
 * 
 * @Date: 2019年12月07日 19:54:14 
 * @author 蒋匿
 */
@Getter
@Setter
@Table(name="t_e_invoice_order_change_record")
public class InvoiceOrderChangeRecordEntity implements Serializable {
	
	private static final long serialVersionUID = -1L;
	
	/**
	 * 主键id
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@ApiModelProperty(value = "主键id")
	private Long id;
	
	/**
	 * 订单号
	 */
	@ApiModelProperty(value = "订单号")
	private String orderNo;
	
	/**
	 * 机构编码
	 */
	@ApiModelProperty(value = "机构编码")
	private String oemCode;
	
	/**
	 * 订单状态 0-待创建 1-待付款 2-待审核 3-出票中 4-待发货 5-出库中 6-待收货 7-已签收  8-已取消
	 */
	@ApiModelProperty(value = "发票类型：0->待创建；1->待付款；2-待审核；3-出票中；4-待发货；5-出库中；6-待收货；7-已签收；8-已取消")
	private Integer orderStatus;
	
	/**
	 * 企业id
	 */
	@ApiModelProperty(value = "企业id")
	private Long companyId;
	
	/**
	 * 开票金额
	 */
	@ApiModelProperty(value = "开票金额")
	private Long invoiceAmount;
	
	/**
	 * 增值税费
	 */
	@ApiModelProperty(value = "增值税费")
	private Long vatFee;

	/**
	 * 增值税税率
	 */
	@ApiModelProperty(value = "增值税税率")
	private BigDecimal vatFeeRate;

	/**
	 * 增值税补缴
	 */
	@ApiModelProperty(value = "增值税补缴")
	private Long vatPayment;

	/**
	 * 增值税计税金额
	 */
	@ApiModelProperty(value = "增值税计税金额")
	private Long vatFeeQuota;
	
	/**
	 * 个人所得税
	 */
	@ApiModelProperty(value = "个人所得税")
	private Long personalIncomeTax;

	/**
	 * 个人所得税税率
	 */
	@ApiModelProperty(value = "个人所得税税率")
	private BigDecimal personalIncomeTaxRate;

	/**
	 * 个人所得税补缴
	 */
	@ApiModelProperty(value = "个人所得税补缴")
	private Long incomeTaxPayment;

	/**
	 * 个人所得税计税金额
	 */
	@ApiModelProperty(value = "个人所得税计税金额")
	private Long personalIncomeTaxQuota;

	/**
	 * 附加税
	 */
	@ApiModelProperty(value = "附加税")
	private Long surcharge;

	/**
	 * 附加税税率
	 */
	@ApiModelProperty(value = "附加税税率")
	private BigDecimal surchargeRate;

	/**
	 * 附加税补缴
	 */
	@ApiModelProperty(value = "附加税补缴")
	private Long surchargePayment;

	/**
	 * 服务费
	 */
	@ApiModelProperty(value = "服务费")
	private Long serviceFee;
	
	/**
	 * 服务费优惠
	 */
	@ApiModelProperty(value = "服务费优惠")
	private Long serviceFeeDiscount;
	
	/**
	 * 发票抬头公司名称
	 */
	@ApiModelProperty(value = "发票抬头公司名称")
	private String companyName;
	
	/**
	 * 发票抬头公司地址
	 */
	@ApiModelProperty(value = "发票抬头公司地址")
	private String companyAddress;
	
	/**
	 * 发票抬头税号
	 */
	@ApiModelProperty(value = "发票抬头税号")
	private String ein;
	
	/**
	 * 发票抬头电话号码
	 */
	@ApiModelProperty(value = "发票抬头电话号码")
	private String phone;
	
	/**
	 * 发票抬头注册地址
	 */
	@ApiModelProperty(value = "发票抬头注册地址")
	private String registAddress;
	
	/**
	 * 发票抬头开户银行
	 */
	@ApiModelProperty(value = "发票抬头开户银行")
	private String bankName;
	
	/**
	 * 发票抬头银行账号
	 */
	@ApiModelProperty(value = "发票抬头银行账号")
	private String bankNumber;
	
	/**
	 * 发票抬头收件人
	 */
	@ApiModelProperty(value = "发票抬头收件人")
	private String recipient;
	
	/**
	 * 发票抬头联系电话
	 */
	@ApiModelProperty(value = "发票抬头联系电话")
	private String recipientPhone;
	
	/**
	 * 发票抬头详细地址
	 */
	@ApiModelProperty(value = "发票抬头详细地址")
	private String recipientAddress;
	
	/**
	 * 开票类目id
	 */
	@ApiModelProperty(value = "开票类目id")
	private Long categoryId;
	
	/**
	 * 开票类目名称
	 */
	@ApiModelProperty(value = "开票类目名称")
	private String categoryName;
	
	/**
	 * 支付金额
	 */
	@ApiModelProperty(value = "支付金额")
	private Long payAmount;
	
	/**
	 * 发票类型 1-普通发票 2-增值税发票
	 */
	@ApiModelProperty(value = "发票类型：1->普通发票；2-增值税发票")
	private Integer invoiceType;
	
	/**
	 * 开票类型名称
	 */
	@ApiModelProperty(value = "开票类型名称")
	private String invoiceTypeName;
	
	/**
	 * 银行流水截图
	 */
	@ApiModelProperty(value = "银行流水截图")
	private String accountStatement;
	
	/**
	 * 邮寄费金额
	 */
	@ApiModelProperty(value = "邮寄费金额")
	private Long postageFees;
	
	/**
	 * 添加时间
	 */
	@ApiModelProperty(value = "添加时间")
	private Date addTime;
	
	/**
	 * 添加人
	 */
	@ApiModelProperty(value = "添加人")
	private String addUser;
	
	/**
	 * 修改时间
	 */
	@ApiModelProperty(value = "修改时间")
	private Date updateTime;
	
	/**
	 * 修改人
	 */
	@ApiModelProperty(value = "修改人")
	private String updateUser;
	
	/**
	 * 备注
	 */
	@ApiModelProperty(value = "备注")
	private String remark;

	/**
	 * 通知次数
	 */
	@ApiModelProperty(value = "通知次数")
	private String alertNumber;

	/**
	 * 发票方式
	 */
	@ApiModelProperty(value = "发票方式 1-纸质发票 2-电子发票")
	private Integer invoiceWay;

	/**
	 * 抬头省编码
	 */
	@ApiModelProperty(value = "抬头省编码")
	private String provinceCode;

	/**
	 * 抬头省名称
	 */
	@ApiModelProperty(value = "抬头省名称")
	private String provinceName;

	/**
	 * 抬头市编码
	 */
	@ApiModelProperty(value = "抬头市编码")
	private String cityCode;

	/**
	 * 抬头市名称
	 */
	@ApiModelProperty(value = "抬头市名称")
	private String cityName;

	/**
	 * 抬头区编码
	 */
	@ApiModelProperty(value = "抬头区编码")
	private String districtCode;

	/**
	 * 抬头区名称
	 */
	@ApiModelProperty(value = "抬头区名称")
	private String districtName;

	/**
	 * 发票图片地址，多个图片之间用逗号分割
	 */
	@ApiModelProperty(value = "发票图片地址，多个图片之间用逗号分割")
	private String invoiceImgs;

	/**
	 * 是否先开票后补流水 0-先开票后补流水 1-先上传流水再开票
	 */
	@ApiModelProperty(value = "是否先开票后补流水 0-先开票后补流水 1-先上传流水再开票")
	private Integer isAfterUploadBankWater;

	/**
	 * 流水状态 0-待上传 1-审核中 2-审核通过 3-审核不通过 4-流水前置
	 */
	@ApiModelProperty(value = "流水状态 0-待上传 1-审核中 2-审核通过 3-审核不通过 4-流水前置")
	private Integer bankWaterStatus;

	/**
	 * '审核失败原因
	 */
	@ApiModelProperty(value = "'审核失败原因")
	private String auditErrorRemark;

	/**
	 * 签收时间
	 */
	@ApiModelProperty(value = "签收时间")
	private Date completeTime;

	/**
	 * 商品名称
	 */
	@ApiModelProperty(value = "商品名称")
	private String goodsName;

	/**
	 * 钱包类型 1-消费钱包 2-佣金钱包
     */
    @ApiModelProperty(value = "钱包类型 1-消费钱包 2-佣金钱包")
    private Integer walletType;

    /**
     * 开票方式 1-自助开票 2-集团代开 3-佣金开票
	 */
	@ApiModelProperty(value = "开票方式 1-自助开票 2-集团代开 3-佣金开票")
	private Integer createWay;

	/**
	 * 业务合同
	 */
	@ApiModelProperty(value = "业务合同")
	private String businessContractImgs;

	/**
	 * 发票备注
	 */
	@ApiModelProperty(value = "发票备注")
	private String invoiceRemark;

	/**
	 * 对公户账号
	 */
	private String corporateAccount;

	/**
	 * 对公户银行
	 */
	private String corporateAccountBankName;

	/**
	 * 出票日期
	 */
	private Date confirmInvoiceTime;

	/**
	 * 应退增值税
	 */
	@ApiModelProperty(value = "需退税费(分)")
	private Long refundableVat;

	/**
	 * 应退附加税
	 */
	@ApiModelProperty(value = "应退附加税(分)")
	private Long refundableSurcharge;

	/**
	 * 应退所得税
	 */
	@ApiModelProperty(value = "应退所得税(分)")
	private Long refundableTax;

	/**
	 * 已缴增值税
	 */
	@ApiModelProperty(value = "已缴增值税")
	private Long paidVatFee;

	/**
	 * 已缴附加税
	 */
	@ApiModelProperty(value = "已缴附加税")
	private Long paidSurcharge;

	/**
	 * 已缴所得税
	 */
	@ApiModelProperty(value = "已缴所得税")
	private Long paidIncomeTax;

	/**
	 * 本年已缴所得税
	 */
	@ApiModelProperty(value = "本年已缴所得税")
	private Long paidIncomeTaxYear;

	/**
	 * 本周期开票金额
	 */
	@ApiModelProperty(value = "本周期开票金额")
	private Long periodInvoiceAmount;

	/**
	 * 本年历史开票金额
	 */
	@ApiModelProperty(value = "本年历史开票金额")
	private Long historicalInvoiceAmount;

	/**
	 * 应税所得率
	 */
	@ApiModelProperty(value = "应税所得率")
	private BigDecimal taxableIncomeRate;

	/**
	 * 风险承诺函，多个图片之间用逗号分割
	 */
	@ApiModelProperty(value = "风险承诺函，多个图片之间用逗号分割")
	private String riskCommitment;
	/**
	 *成果状态 0-无需上传 1-成果前置 2-待上传 3-审核中 4-审核不通过 5-审核通过
	 */
	private Integer achievementStatus;
	/**
	 * 成果图片，多个图片之间用逗号分割
	 */
	private String achievementImgs;
	/**
	 * 成果视频
	 */
	private String achievementVideo;
	/**
	 * 成果审核失败原因
	 */
	private String achievementErrorRemark;
	/**
	 * 支付方式 1-线上支付 2-线下支付
	 */
	private Integer payType;

	/**
	 * 补充说明
	 */
	private String supplementExplain;

	/**
	 * 商品明细json
	 */
	private String goodsDetails;

	/**
	 * 支付凭证
	 */
	private String paymentVoucher;

	/**
	 * 作废/红冲凭证
	 */
	private String cancellationVoucher;

	/**
	 * 发票标识 0-正常 1-已作废/红冲 2-作废重开
	 */
	private Integer invoiceMark;

	/**
	 * 作废时间
	 */
	private Date cancellationTime;

	/**
	 * 作废/红冲说明
	 */
	private String cancellationRemark;

	/**
	 * 作废重开关联订单号
	 */
	private String relevanceOrderNo;

	/**
	 * 服务费是否已重新计算 0-否 1-是
	 */
	private Integer isRecalculateServiceFee;

	/**
	 * 纳税人类型  1-小规模纳税人 2-一般纳税人
	 */
	private Integer taxpayerType;

	/**
	 * 发票是否已重开 0-否 1-是
	 */
	private Integer isReopen;

	/**
	 * 印花税税率
	 */
	private BigDecimal stampDutyRate;

	/**
	 * 应缴印花税金额
	 */
	private Long stampDutyAmount;

	/**
	 * 水利建设基金税率
	 */
	private BigDecimal waterConservancyFundRate;

	/**
	 * 应缴水利建设基金金额
	 */
	private Long waterConservancyFundAmount;

	/**
	 * 收款凭证
	 */
	private String receiptPaymentVoucher;
}
