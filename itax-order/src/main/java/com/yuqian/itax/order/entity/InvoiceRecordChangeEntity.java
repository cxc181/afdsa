package com.yuqian.itax.order.entity;

import java.io.Serializable;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;
/**
 * 开票记录变更表
 * 
 * @Date: 2020年12月25日 11:42:11 
 * @author 蒋匿
 */
@Getter
@Setter
@Table(name="t_e_invoice_record_change")
public class InvoiceRecordChangeEntity implements Serializable {
	
	private static final long serialVersionUID = -1L;
	
	/**
	 * 主键id
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	/**
	 * 订单号
	 */
	private String orderNo;

	/**
	 * 企业id
	 */
	private Long companyId;
	
	/**
	 * 机构编码
	 */
	private String oemCode;
	
	/**
	 * 开票记录编号
	 */
	private String invoiceRecordNo;

	/**
	 * 渠道流水号
	 */
	private String tradeNo;
	
	/**
	 * 开票金额(分)
	 */
	private Long invoiceAmount;
	
	/**
	 * 状态 0-待提交、1-人工出票、2-待补票、3-出票中断、4-出票失败、5-推送失败、6-待确认、7-已完成 8-已取消
	 */
	private Integer status;
	
	/**
	 * 处理方式 1-线下、2-托管
	 */
	private Integer handlingWay;
	
	/**
	 * 园区id
	 */
	private Long parkId;
	
	/**
	 * 描述
	 */
	private String invoiceDesc;
	
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
	 * 完成时间
	 */
	private Date completeTime;
	
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
	
	
}
