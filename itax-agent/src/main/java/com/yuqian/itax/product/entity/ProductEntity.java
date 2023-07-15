package com.yuqian.itax.product.entity;

import java.io.Serializable;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;
/**
 * 产品管理
 * 
 * @Date: 2019年12月07日 20:41:26 
 * @author 蒋匿
 */
@Getter
@Setter
@Table(name="t_e_product")
public class ProductEntity implements Serializable {
	
	private static final long serialVersionUID = -1L;
	
	/**
	 * 主键id
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	/**
	 * 产品名称
	 */
	private String prodName;
	
	/**
	 * 产品编码
	 */
	private String prodCode;
	
	/**
	 * 产品类型 1-个体开户 2-个独开户 3-有限合伙开户 4-有限责任开户  5-个体开票 6-个独开票 7-有限合伙开票 8-有限责任开票 9-黄金会员（废弃） 10-钻石会员 （废弃）11-个体注销 12-个独注销 13-有限合伙注销 14-有限责任注销  15-公户申请和托管 16-个体托管费续费
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
	 * 产品描述
	 */
	private String prodDesc;
	
	/**
	 * 状态 0-待上架 1-已上架 2-已下架 3-已暂停
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
	 * 机构名称
	 */
	@Transient
	private String oemName;

	/**
	 * 注销累计开票额度
	 */
	private Long cancelTotalLimit;

	/**
	 * 办理费（对公户独有）
	 */
	private Long processingFee;

	/**
	 * 企业类型1-个体工商户 2-个人独资企业 3-有限合伙 4-有限责任
	 */
	private Integer companyType;

	/**
	 * 收费标准协议模板id
	 */
	private Long agreementTemplateId;
}
