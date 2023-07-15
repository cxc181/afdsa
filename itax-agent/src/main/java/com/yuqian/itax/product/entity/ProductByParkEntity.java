package com.yuqian.itax.product.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;
/**
 * 产品园区定价
 * 
 * @Date: 2022年04月11日 10:57:21 
 * @author 蒋匿
 */
@Getter
@Setter
@Table(name="t_e_product_by_park")
public class ProductByParkEntity implements Serializable {
	
	private static final long serialVersionUID = -1L;
	
	/**
	 * 主键id
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	/**
	 * 产品id
	 */
	private Long productId;
	
	/**
	 * 园区id
	 */
	private Long parkId;
	
	/**
	 * 产品类型 1-个体开户 2-个独开户 3-有限合伙开户 4-有限责任开户  5-个体开票 6-个独开票 7-有限合伙开票 8-有限责任开票    9-黄金会员（废弃） 10-钻石会员 （废弃）
11-个体注销 12-个独注销 13-有限合伙注销 14-有限责任注销 15-公户申请和托管 16-个体托管费续费 17-对公户年费续费
	 */
	private Integer prodType;
	
	/**
	 * 金额
	 */
	private Long prodAmount;
	
	/**
	 * 金额名称
	 */
	private String amountName;
	
	/**
	 * 费用方式 1-固定金额 2-比率
	 */
	private Integer amountWay;
	
	/**
	 * 机构编码
	 */
	private String oemCode;
	
	/**
	 * 是否删除 0-未删除 1-已删除
	 */
	private Integer isDelete;
	
	/**
	 * 办理费（对公户独有）
	 */
	private Long processingFee;
	
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
	 * 注销累计开票额度
	 */
	private Long cancelTotalLimit;
	
	
}
