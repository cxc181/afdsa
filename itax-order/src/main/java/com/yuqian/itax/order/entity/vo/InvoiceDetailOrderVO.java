package com.yuqian.itax.order.entity.vo;

import com.yuqian.itax.agent.entity.InvoiceInfoByOemEntity;
import com.yuqian.itax.order.entity.ConsumptionInvoiceOrderEntity;
import com.yuqian.itax.order.entity.InvoiceOrderEntity;
import com.yuqian.itax.user.entity.MemberCompanyEntity;
import com.yuqian.itax.util.util.MoneyUtil;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 开票订单
 * 
 * @Date: 2019年12月07日 20:05:12 
 * @author 蒋匿
 */
@Getter
@Setter
public class InvoiceDetailOrderVO implements Serializable {
	
	private static final long serialVersionUID = -1L;

	/**
	 * 订单号
	 */
	private String orderNo;
	/**
	 * 开票企业
	 */
	private String invCompanyName;
	/**
	 * 开票企业id
	 */
	private Long invCompanyId;
	/**
	 * 经营者名称
	 */
	private String operatorName;
	/**
	 * 经营者手机号
	 */
	private String operatorPhone;
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
	 * 开票金额
	 */
	private Long invoiceAmount;
	/**
	 * 开票类型 1-普通发票 2-增值税发票
	 */
	private Integer invoiceType;
	/**
	 * 发票类型：1->纸质发票；2-电子发票
	 */
	private Integer invoiceWay;
	/**
	 * 开票类目名称
	 */
	private String categoryName;

	/**
	 * 开票类目id
	 */
	private Long categoryId;

	/**
	 * 发票图片
	 */
	private String invoiceImgs;
	/**
	 * 发票图片
	 */
	private List<String> invoiceImgsList;

	/**
	 * 银行流水截图
	 */
	private String accountStatement;
	/**
	 * 银行流水截图
	 */
	private List<String> accountStatementList;

	/**
	 * 快递单号
	 */
	private String courierNumber;

	/**
	 * 快递公司名称
	 */
	private String courierCompanyName;

	/**
	 * 流水状态 0-待上传 1-审核中 2-审核通过 3-审核不通过 4-流水前置
	 */
	private Integer bankWaterStatus;

	/**
	 * 是否先开票后补流水 0-先开票后补流水 1-先上传流水再开票
	 * 是否流水前置 0：否，1：是
	 */
	private Integer isAfterUploadBankWater;

	/**
	 * 商品名称
	 */
	private String goodsName;

	/**
	 * 业务合同
	 */
	private String businessContractImgs;

	/**
	 * 业务合同集合
	 */
	private List<String> businessContractImgList;

	/**
	 * 增值税税率
	 */
	private BigDecimal vatFeeRate;

	/**
	 * 发票备注
	 */
	private String invoiceRemark;
	/**
	 * 收票邮箱
	 */
	private String email;

	/**
	 * 订单附件集合
	 */
	private List<String> attachmentImgList;

	/**
	 * 成果图片集合
	 */
	private  List<String>  achievementImgList;

	/**
	 * 成果图片
	 */
	private String achievementImgs;

	/**
	 * 成果视频长链
	 */
	private String 	achievementVideoLeng;

	/**
	 * 成果视频短链
	 */
	private String 	achievementVideo;

	/**
	 * 对公户账号
	 */
	private String corporateAccount;

	/**
	 * 补充说明
	 */
	private String supplementExplain;


	/**
	 * 对公户银行
	 */
	private String corporateAccountBankName;

	/**
	 * 收件邮箱
	 */
	private String billToEmail;

	/**
	 * 一般纳税人资质
	 */
	private String generalTaxpayerQualification;

	/**
	 * 成果状态 0-无需上传 1-成果前置 2-待上传 3-审核中 4-审核不通过 5-审核通过
	 */
	private Integer achievementStatus;

	/**
	 * 作废重开关联订单号
	 */
	private String relevanceOrderNo;

	/**
	 * 发票标识 0-正常 1-已作废/红冲 2-作废重开
	 */
	private Integer invoiceMark;

	/**
	 * 本月开票金额
	 */
	private Long MonthInvoiceAmount;

	/**
	 * 作废/红冲说明
	 */
	private String cancellationRemark;

	/**
	 * 开票类目类型  1 系统开票类目  2用户自定义开票类目
	 */
	private Integer categoryType;

	/**
	 * 营业执照
	 */
	private String businessLicense;

	/**
	 * 是否需要审核不通过风险提示 0-否 1-是
	 */
	private Integer isRiskHint;

	/**
	 * 本周期开票金额
	 */
	private Long periodInvoiceAmount;

	/**
	 * 纳税人类型  1-小规模纳税人 2-一般纳税人
	 */
	private Integer taxpayerType;

	public InvoiceDetailOrderVO() {
	}

	public InvoiceDetailOrderVO(ConsumptionInvoiceOrderEntity consumptionInvoiceOrderEntity, InvoiceInfoByOemEntity invoiceInfoByOemEntity){
		this.invoiceAmount = consumptionInvoiceOrderEntity.getInvoiceAmount();
		this.categoryName = consumptionInvoiceOrderEntity.getCategoryName();
		this.invoiceWay = consumptionInvoiceOrderEntity.getInvoiceWay();
		this.vatFeeRate = invoiceInfoByOemEntity.getVatRate()== null ?BigDecimal.ZERO : invoiceInfoByOemEntity.getVatRate();
		this.companyName = consumptionInvoiceOrderEntity.getCompanyName();
		this.ein = consumptionInvoiceOrderEntity.getEin();
		this.phone = consumptionInvoiceOrderEntity.getPhone();
		this.bankName = consumptionInvoiceOrderEntity.getBankName();
		this.bankNumber = consumptionInvoiceOrderEntity.getBankNumber();
		this.companyAddress = consumptionInvoiceOrderEntity.getCompanyAddress();
		this.recipient = consumptionInvoiceOrderEntity.getRecipient();
		this.recipientPhone = consumptionInvoiceOrderEntity.getRecipientPhone();
		this.recipientAddress = consumptionInvoiceOrderEntity.getRecipientAddress();
		this.billToEmail = consumptionInvoiceOrderEntity.getBillToEmail();
		this.generalTaxpayerQualification = consumptionInvoiceOrderEntity.getGeneralTaxpayerQualification();
	}
	public InvoiceDetailOrderVO(InvoiceOrderEntity invEntity, MemberCompanyEntity company) {
		this.invCompanyName = company.getCompanyName();
		this.invCompanyId = company.getId();
		this.operatorName = company.getOperatorName();
		this.companyName = invEntity.getCompanyName();
		this.companyAddress = invEntity.getCompanyAddress();
		this.ein = invEntity.getEin();
		this.phone = invEntity.getPhone();
		this.bankName = invEntity.getBankName();
		this.bankNumber = invEntity.getBankNumber();
		this.recipient = invEntity.getRecipient();
		this.recipientPhone = invEntity.getRecipientPhone();
		this.recipientAddress = (invEntity.getProvinceName()==null?"":invEntity.getProvinceName()) + (invEntity.getCityName()==null?"":invEntity.getCityName()) +(invEntity.getDistrictName()==null?"":invEntity.getDistrictName()) + (invEntity.getRecipientAddress()==null?"":invEntity.getRecipientAddress());
		this.invoiceAmount = invEntity.getInvoiceAmount();
		this.invoiceType = invEntity.getInvoiceType();
		this.categoryName = invEntity.getCategoryName();
		this.categoryId = invEntity.getCategoryId();
		this.operatorPhone = company.getOperatorTel();
		this.invoiceWay = invEntity.getInvoiceWay();
		this.courierNumber = invEntity.getCourierNumber();
		this.courierCompanyName = invEntity.getCourierCompanyName();
		this.bankWaterStatus = invEntity.getBankWaterStatus();
		this.isAfterUploadBankWater = invEntity.getIsAfterUploadBankWater();
		this.goodsName = invEntity.getGoodsName();
		this.businessContractImgs = invEntity.getBusinessContractImgs();
		this.accountStatement = invEntity.getAccountStatement();
		this.vatFeeRate = invEntity.getVatFeeRate() == null ? BigDecimal.ZERO : MoneyUtil.yuan2fen(invEntity.getVatFeeRate());
		this.invoiceRemark = invEntity.getInvoiceRemark();
		this.email = invEntity.getEmail();
		this.corporateAccount = invEntity.getCorporateAccount();
		this.corporateAccountBankName = invEntity.getCorporateAccountBankName();
		this.supplementExplain=invEntity.getSupplementExplain();
		this.relevanceOrderNo = invEntity.getRelevanceOrderNo();
		this.invoiceMark = invEntity.getInvoiceMark();
		this.periodInvoiceAmount = invEntity.getPeriodInvoiceAmount();
		this.taxpayerType = invEntity.getTaxpayerType();
	}
}
