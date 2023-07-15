package com.yuqian.itax.snapshot.entity;

import java.io.Serializable;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;
/**
 * 开票订单快照(已完成)
 * 
 * @Date: 2020年10月26日 11:25:23 
 * @author 蒋匿
 */
@Getter
@Setter
@Table(name="t_e_invoice_order_snapshot")
public class InvoiceOrderSnapshotEntity implements Serializable {
	
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
	 * 用户id
	 */
	private Long userId;
	
	/**
	 * 园区id
	 */
	private Long parkId;
	
	/**
	 * 机构编码
	 */
	private String oemCode;
	
	/**
	 * 企业id
	 */
	private Long companyId;
	
	/**
	 * 用户类型 1-会员 2-城市合伙人 3-高级城市合伙人 4-平台 5-管理员 6-其他
	 */
	private Integer userType;
	
	/**
	 * 开票金额(分)
	 */
	private Long invoiceAmount;
	
	/**
	 * 发票类型 1-增值税普通发票 2-增值税专用发票
	 */
	private Integer invoiceType;
	
	/**
	 * 发票方式 1-纸质发票 2-电子发票
	 */
	private Integer invoiceWay;
	
	/**
	 * 总税费(分)
	 */
	private Long totalTaxFee;
	
	/**
	 * 增值税(分)
	 */
	private Long vatFee;
	
	/**
	 * 增值税补缴(分)
	 */
	private Long vatPayment;
	
	/**
	 * 所得税补缴(分)
	 */
	private Long incomeTaxPayment;
	
	/**
	 * 附加税(分)
	 */
	private Long surcharge;
	
	/**
	 * 附加税补缴(分)
	 */
	private Long surchargePayment;
	
	/**
	 * 已退税费(分)
	 */
	private Long refundTaxFee;
	
	/**
	 * 快递费(分)
	 */
	private Long postageFees;
	
	/**
	 * 服务费(分)
	 */
	private Long serviceFee;
	
	/**
	 * 所得税(分)
	 */
	private Long personalIncomeTax;
	
	/**
	 * 可分润金额(分)
	 */
	private Long profitAmount;
	
	/**
	 * 机构利润(分)
	 */
	private Long oemProfits;
	
	/**
	 * 快照时间(订单完成时间)
	 */
	private Date snapshotTime;
	
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
