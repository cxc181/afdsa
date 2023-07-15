package com.yuqian.itax.order.entity.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 开票订单
 * 
 * @Date: 2019年12月12日 20:05:12
 * @author yejian
 */
@Getter
@Setter
public class InvoiceOrderSubpackageVO implements Serializable {
	
	private static final long serialVersionUID = -1L;

	/**
	 * 订单号
	 */
	@ApiModelProperty(value = "订单号")
	private String orderNo;

	/**
	 * 集团开票订单号
	 */
	@ApiModelProperty(value = "集团开票订单号")
	private String groupOrderNo;

	/**
	 * 订单状态 0-待创建 1-待付款 2-待审核 3-出票中 4-待发货 5-出库中 6-待收货 7-已签收  8-已取消
	 */
	@ApiModelProperty(value = "发票类型：0->待创建；1->待付款；2-待审核；3-出票中；4-待发货；5-出库中；6-待收货；7-已签收；8-已取消")
	private Integer orderStatus;

	/**
	 * 机构编码
	 */
	@ApiModelProperty(value = "机构编码")
	private String oemCode;

	/**
	 * 用户id
	 */
	@ApiModelProperty(value = "用户id")
	private Long userId;

	/**
	 * 开票公司id
	 */
	@ApiModelProperty(value = "开票公司id")
	private Long companyId;

	/**
	 * 开票公司名称
	 */
	@ApiModelProperty(value = "开票公司名称")
	private String companyName;

	/**
	 * 开票类目名称
	 */
	@ApiModelProperty(value = "开票类目名称")
	private String categoryName;

	/**
	 * 发票类型 1-普通发票 2-增值税发票
	 */
	@ApiModelProperty(value = "发票类型：1->普通发票；2-增值税发票")
	private Integer invoiceType;

	/**
	 * 发票方式
	 */
	@ApiModelProperty(value = "发票方式：1->纸质发票；2-电子发票")
	private Integer invoiceWay;

	/**
	 * 开票类型名称
	 */
	@ApiModelProperty(value = "开票类型名称")
	private String invoiceTypeName;

	/**
	 * 发票抬头公司名称
	 */
	@ApiModelProperty(value = "发票抬头公司名称")
	private String invHeadCompanyName;

	/**
	 * 流水状态 0-待上传 1-审核中 2-审核通过 3-审核不通过 4-流水前置
	 */
	@ApiModelProperty(value = "流水状态 0-待上传 1-审核中 2-审核通过 3-审核不通过 4-流水前置")
	private Integer bankWaterStatus;

	/**
	 * 银行流水截图
	 */
	@ApiModelProperty(value = "银行流水截图")
	private String accountStatement;

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
	 * 发票抬头收件地址
	 */
	@ApiModelProperty(value = "发票抬头收件地址")
	private String recipientAddress;

	/**
	 * 快递单号
	 */
	@ApiModelProperty(value = "快递单号")
	private String courierNumber;

	/**
	 * 快递公司名称
	 */
	@ApiModelProperty(value = "快递公司名称")
	private String courierCompanyName;

	/**
	 * 审核失败原因
	 */
	@ApiModelProperty(value = "审核失败原因")
	private String auditErrorRemark;

	/**
	 * 备注
	 */
	@ApiModelProperty(value = "备注")
	private String remark;

	/**
	 * t_e_invoice_record表的remark字段
	 */
	@ApiModelProperty(value = "出票失败原因")
	private String invremark;

	/**
	 * 添加时间
	 */
	@ApiModelProperty(value = "添加时间")
	private Date addTime;

	/**
	 * 修改时间
	 */
	@ApiModelProperty(value = "修改时间")
	private Date updateTime;

	/**
	 * 发票抬头省名称
	 */
	@ApiModelProperty(value = "发票抬头省名称")
	private String provinceName;

	/**
	 * 发票抬头市名称
	 */
	@ApiModelProperty(value = "发票抬头市名称")
	private String cityName;

	/**
	 * 发票抬头区名称
	 */
	@ApiModelProperty(value = "发票抬头区名称")
	private String districtName;

	/**
	 * 开票金额
	 */
	@ApiModelProperty(value = "开票金额")
	private Long invoiceAmount;

	/**
	 * 支付金额
	 */
	@ApiModelProperty(value = "支付金额")
	private Long payAmount;

	/**
	 * 增值税计税金额
	 */
	@ApiModelProperty(value = "增值税计税金额")
	private Long vatFeeQuota;

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
	 * 合计增值税税费（增值税税费+补缴增值税税费）
	 */
	@ApiModelProperty(value = "合计增值税税费（增值税税费+补缴增值税税费）")
	private Long allVatTax;

	/**
	 * 个人所得税计税金额
	 */
	@ApiModelProperty(value = "个人所得税计税金额")
	private Long personalIncomeTaxQuota;

	/**
	 * 个人所得税应纳税所得额
	 */
	@ApiModelProperty(value = "个人所得税应纳税所得额")
	private Long personalIncomeTaxable;

	/**
	 * 个人所得税费
	 */
	@ApiModelProperty(value = "个人所得税费")
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
	 * 个人所得税需退税费
	 */
	@ApiModelProperty(value = "个人所得税需退税费")
	private Long personalIncomeTaxRefund;

	/**
	 * 合计个人所得税税费（个人所得税税费+补缴个人所得税税费）
	 */
	@ApiModelProperty(value = "合计个人所得税税费（个人所得税税费+补缴个人所得税税费）")
	private Long allIncomeTax;

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
	 * 合计附加税税费（附加税税费+补缴附加税税费）
	 */
	@ApiModelProperty(value = "合计附加税税费（附加税税费+补缴附加税税费）")
	private Long allSurchargeTax;

	/**
	 * 合计所有税费（合计增值税税费+合计个人所得税税费+合计附加税税费）
	 */
	@ApiModelProperty(value = "合计所有税费（合计增值税税费+合计个人所得税税费+合计附加税税费）")
	private Long allTax;

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
	 * 邮寄费
	 */
	@ApiModelProperty(value = "邮寄费")
	private Long postageFees;

	/*
	 * 经营者手机号
	 */
	@ApiModelProperty(value = "经营者手机号")
	private String operatorTel;

	/**
	 * 是否为他人办理 0-本人办理 1-为他人办理
	 */
	@ApiModelProperty(value = "是否为他人办理 0->本人办理 1->为他人办理")
	private Integer isOther;

	/**
	 * 商品名称
	 */
	@ApiModelProperty(value = "商品名称")
	private String goodsName;

	/**
	 * 业务合同
	 */
	@ApiModelProperty(value = "业务合同")
	private String businessContractImgs;

	/**
	 * 收票邮箱地址
	 */
	@ApiModelProperty(value = "收票邮箱")
	private String email;

	/**
	 * 本年历史开票金额
	 */
	@ApiModelProperty(value = "本年历史开票金额")
	private Long historicalInvoiceAmount;

	/**
	 * 开票方式 1-自助开票 2-集团代开 3-佣金开票
	 */
	@ApiModelProperty(value = "开票方式 1-自助开票 2-集团代开 3-佣金开票")
	private Integer createWay;

	/**
	 * 税费明细
	 */
	@ApiModelProperty(value = "税费明细")
	private TaxFeeDetailVO taxFeeDetail;

	/**
	 * 服务费明细
	 */
	@ApiModelProperty(value = "服务费明细")
	private List<InvoiceServiceFeeDetailVO> invoiceServiceFeeDetail;

	/**
	 * 成果图片
	 */
	@ApiModelProperty(value = "成果图片")
	private String achievementImgs;

	/**
	 * 成果视频
	 */
	@ApiModelProperty(value = "成果视频")
	private String achievementVideo;

	/**
	 * 成果状态 0-无需上传 1-成果前置 2-待上传 3-审核中 4-审核不通过 5-审核通过
	 */
	@ApiModelProperty(value = "成果状态")
	private Integer achievementStatus;

	/**
	 * 银行流水截图列表
	 */
	private String[] accountStatementList;
	/**
	 * 业务合同截图列表
	 */
	private String[] businessContractImgsList;
	/**
	 * 成果图片列表
	 */
	private String[] achievementImgsList;
	/**
	 * 成果视频列表
	 */
    private String[] achievementVideoList;

	/**
	 * 发票备注
	 */
	private String invoiceRemark;

	/**
	 * 补充说明
	 */
	private String supplementExplain;

	/**
	 * 发票标识 0-正常 1-已作废/红冲 2-作废重开
	 */
	private Integer invoiceMark;
}