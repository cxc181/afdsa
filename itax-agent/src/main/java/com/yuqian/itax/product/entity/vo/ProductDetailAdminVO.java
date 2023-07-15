package com.yuqian.itax.product.entity.vo;

import com.yuqian.itax.product.entity.ProductEntity;
import com.yuqian.itax.product.entity.dto.ProductAndParkDTO;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

/**
 * 产品管理
 * 
 * @Date: 2019年12月07日 20:41:26 
 * @author 蒋匿
 */
@Getter
@Setter
public class ProductDetailAdminVO implements Serializable {
	
	private static final long serialVersionUID = -1L;
	
	/**
	 * 主键id
	 */
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
	 * 产品类型 1-个体开户 2-个独开户 3-有限合伙开户 4-有限责任开户  5-个体开票 6-个独开票 7-有限合伙开票 8-有限责任开票    9-税务顾问 10-城市服务商
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
	 * 备注
	 */
	private String remark;

	/**
	 * 园区id集合
	 */
	private List<Long> parkIds;

	/**
	 * 收费标准集合
	 */
	private List<ChargeStandardVO> jsonStr;

	/**
	 * 收费标准协议模板id
	 */
	private Long chargeStandardTemplateId;
	/**
	 * 园区单独定价
	 */
	private List<ProductAndParkDTO> productByParkList;

	/**
	 * 注销累计开票额度
	 */
	private Long cancelTotalLimit;

	/**
	 * 办理费（对公户独有）
	 */
	private Long processingFee;

	/**
	 * 模板信息
	 */
	private List<ProductParkAgreementTemplateVO> templateVOList;

	public ProductDetailAdminVO(ProductEntity entity) {
		this.id = entity.getId();
		this.prodName = entity.getProdName();
		this.prodCode = entity.getProdCode();
		this.prodType = entity.getProdType();
		this.prodAmount = entity.getProdAmount();
		this.amountName = entity.getAmountName();
		this.amountWay = entity.getAmountWay();
		this.oemCode = entity.getOemCode();
		this.prodDesc = entity.getProdDesc();
		this.status = entity.getStatus();
		this.remark = entity.getRemark();
		this.cancelTotalLimit = entity.getCancelTotalLimit();
		this.processingFee = entity.getProcessingFee();
		this.chargeStandardTemplateId = entity.getAgreementTemplateId();
	}

	public ProductDetailAdminVO() {
	}
}
