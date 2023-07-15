package com.yuqian.itax.order.entity;

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
 * 开票订单与商品的关系表
 * 
 * @Date: 2021年11月09日 17:24:24 
 * @author 蒋匿
 */
@Getter
@Setter
@Table(name="t_r_invoiceorder_goodsdetail")
public class InvoiceorderGoodsdetailRelaEntity implements Serializable {
	
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
	 * 商品名称
	 */
	private String goodsName;

	/**
	 * 税收分类编码
	 */
	private String taxClassificationCode;
	
	/**
	 * 规格型号
	 */
	private String goodsSpecification;
	
	/**
	 * 计量单位
	 */
	private String goodsUnit;
	
	/**
	 * 商品数量
	 */
	private BigDecimal goodsQuantity;
	
	/**
	 * 商品单价
	 */
	private BigDecimal goodsPrice;
	
	/**
	 * 总金额
	 */
	private Long goodsTotalPrice;
	
	/**
	 * 税率
	 */
	private BigDecimal goodsTaxRate;
	
	/**
	 * 总税费
	 */
	private Long goodsTotalTax;
	
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
