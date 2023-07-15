package com.yuqian.itax.order.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;
/**
 * 开票明细表
 * 
 * @Date: 2020年12月25日 11:42:18 
 * @author 蒋匿
 */
@Getter
@Setter
@Table(name="t_e_invoice_record_detail")
public class InvoiceRecordDetailEntity implements Serializable {
	
	private static final long serialVersionUID = -1L;
	
	/**
	 * 主键id
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	/**
	 * 开票记录编号
	 */
	private String invoiceRecordNo;

	/**
	 * 请求号
	 */
	private String requestNo;
	
	/**
	 * 开票流水号
	 */
	private String invoiceTradeNo;
	
	/**
	 * 发票代码
	 */
	private String invoiceCode;
	
	/**
	 * 发票号码
	 */
	private String invoiceNo;
	
	/**
	 * 校验码
	 */
	private String invoiceCheckCode;
	
	/**
	 * 开票日期
	 */
	private Date invoiceDate;
	
	/**
	 * 二维码
	 */
	private String invoiceQrCode;
	
	/**
	 * 税控码
	 */
	private String taxControlCode;
	
	/**
	 * 发票类型代码 004：增值税专用发票；007：增值税普通发票 ；026：增值税电子发票；028:增值税电子专用发票
	 */
	private String invoiceTypeCode;
	
	/**
	 * 电子发票地址
	 */
	private String einvoiceUrl;

	/**
	 * oss电子发票pdf地址
	 */
	private String einvoiceOssPdfUrl;

	/**
	 * oss电子发票图片地址
	 */
	private String einvoiceOssImgUrl;
	
	/**
	 * 合计金额
	 */
	private Long invoiceTotalPrice;
	
	/**
	 * 合计税额
	 */
	private Long invoiceTotalTax;
	
	/**
	 * 价税合计
	 */
	private Long invoiceTotalPriceTax;
	
	/**
	 * 发票明细集合
	 */
	private String invoiceDetailsList;
	
	/**
	 * 描述
	 */
	private String detailDesc;
	
	/**
	 * 状态 0-正常，1-已打印，2-已作废，3-出票失败 4-出票中 5-待出票
	 */
	private Integer status;
	
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
	 * 是否已生成版式 1-生成中 2-已生成 3-生成失败
	 */
	private Integer IsFormatCreate;

	/**
	 * 商品明细json
	 */
	private String goodsDetails;

	/**
	 * 是否打印清单 1-打印 0-不打印
	 */
	private Integer isPrintDetail;
}
