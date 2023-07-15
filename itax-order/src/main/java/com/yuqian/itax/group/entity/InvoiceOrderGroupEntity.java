package com.yuqian.itax.group.entity;

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
 * 集团开票订单
 * 
 * @Date: 2020年03月04日 09:25:55 
 * @author 蒋匿
 */
@Getter
@Setter
@Table(name="t_e_invoice_order_group")
public class InvoiceOrderGroupEntity implements Serializable {
	
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
	 * 机构编码
	 */
	private String oemCode;
	
	/**
	 * 开票金额
	 */
	private Long invoiceAmount;
	
	/**
	 * 订单状态 0-流水解析中 1-出票中 2-已签收 3-已取消
	 */
	private Integer orderStatus;
	
	/**
	 * 增值税费
	 */
	private Long vatFee;
	
	/**
	 * 个人所得税
	 */
	private Long personalIncomeTax;
	
	/**
	 * 服务费
	 */
	private Long serviceFee;
	
	/**
	 * 服务费优惠
	 */
	private Long serviceFeeDiscount;
	
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
	 * 抬头电话号码
	 */
	private String phone;
	
	/**
	 * 抬头注册地址
	 */
	private String registAddress;
	
	/**
	 * 抬头开户银行
	 */
	private String bankName;
	
	/**
	 * 抬头银行账号
	 */
	private String bankNumber;
	
	/**
	 * 抬头收件人
	 */
	private String recipient;
	
	/**
	 * 抬头联系电话
	 */
	private String recipientPhone;
	
	/**
	 * 抬头详细地址
	 */
	private String recipientAddress;
	
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
	 * 开票基础类目id
	 */
	private Long categoryGroupId;
	
	/**
	 * 开票基础类目名称
	 */
	private String categoryGroupName;
	
	/**
	 * 发票类型 1-普通发票 2-增值税发票
	 */
	private Integer invoiceType;
	
	/**
	 * 发票方式 1-纸质发票 2-电子发票
	 */
	private Integer invoiceWay;
	
	/**
	 * 开票类型名称
	 */
	private String invoiceTypeName;
	
	/**
	 * 银行流水截图
	 */
	private String accountStatement;
	
	/**
	 * 快递单号
	 */
	private String courierNumber;
	
	/**
	 * 快递公司名称
	 */
	private String courierCompanyName;
	
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
	 * 所得税补缴
	 */
	private Long incomeTaxPayment;

	/**
	 * 增值税税率
	 */
	private BigDecimal vatFeeRate;
	/**
	 * 收票邮箱
	 */
	private String email;

	/**
	 * 付款截图
	 */
	private String payImgUrl;


	/**
	 * 财务审核备注
	 */
	private String auditDesc;
}
