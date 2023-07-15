package com.yuqian.itax.order.entity;

import java.io.Serializable;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;
/**
 * 消费开票订单变更记录表
 * 
 * @Date: 2020年09月27日 11:22:42 
 * @author 蒋匿
 */
@Getter
@Setter
@Table(name="t_e_consumption_invoice_order_change")
public class ConsumptionInvoiceOrderChangeEntity implements Serializable {
	
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
	 * 开票类目id
	 */
	private Long categoryId;

	/**
	 * 开票类目名称
	 */
	private String categoryName;
	
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
	 * 完成时间
	 */
	private Date completeTime;
	
	/**
	 * 收票邮箱
	 */
	private String billToEmail;
	
	/**
	 * 关联消费订单号,多个订单之间用 逗号 分割
	 */
	private String consumptionOrderRela;
	
	/**
	 * 消费开票备注
	 */
	private String consumptionInvoiceRemark;

	/**
	 * 发票图片地址，多个图片之间用逗号分割
	 */
	private String invoiceImgs;

	/**
	 * 开票操作人
	 */
	private String invoiceOperator;
	
	/**
	 * 订单状态： 0-待出票 1-出票中 2-已出票 3-出票失败
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
	 * 一般纳税人资质
	 */
	private String generalTaxpayerQualification;
}
