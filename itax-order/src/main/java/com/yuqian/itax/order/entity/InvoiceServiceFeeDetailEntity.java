package com.yuqian.itax.order.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;
/**
 * 服务费收费阶段明细
 * 
 * @Date: 2021年03月16日 14:51:12 
 * @author 蒋匿
 */
@Getter
@Setter
@Table(name="t_e_invoice_service_fee_detail")
public class InvoiceServiceFeeDetailEntity implements Serializable {
	
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
	 * 阶段金额(分)
	 */
	private Long phaseAmount;
	
	/**
	 * 适用费率
	 */
	private BigDecimal feeRate;
	
	/**
	 * 服务费(分)
	 */
	private Long feeAmount;
	
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
