package com.yuqian.itax.order.entity.vo;

import lombok.Getter;
import lombok.Setter;

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
public class InvoiceRecordDetailVO implements Serializable {
	
	private static final long serialVersionUID = -1L;
	
	/**
	 * 主键id
	 */
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
	 * 税控码
	 */
	private String taxControlCode;
	
	/**
	 * 发票类型代码
	 */
	private String invoiceTypeCode;
	
	/**
	 * 电子发票地址
	 */
	private String einvoiceUrl;

	/**
	 * oss电子发票图片地址
	 */
	private String eInvoiceOssImgUrl;
	
	/**
	 * 价税合计
	 */
	private Long invoiceTotalPriceTax;
	
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
	 * 开票记录状态
	 */
	private Integer recordStatus;

	/**
	 * 发票方式 1-纸质发票 2-电子发票
	 */
	private String invoiceWay;

	/**
	 * 是否打印清单 1-打印 0-不打印
	 */
	private Integer isPrintDetail;
}
