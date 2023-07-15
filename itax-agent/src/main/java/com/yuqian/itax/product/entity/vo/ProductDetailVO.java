package com.yuqian.itax.product.entity.vo;

import com.yuqian.itax.park.entity.vo.ParkVO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

/**
 *  @Author: Kaven
 *  @Date: 2019/12/10 11:13
 *  @Description: 产品详情（园区列表）实体类
 */
@Getter
@Setter
public class ProductDetailVO implements Serializable {
	
	private static final long serialVersionUID = -1L;

	@ApiModelProperty(value = "产品ID",required = true)
	private Long productId;// 产品ID
	
	/**
	 * 产品名称
	 */
	@ApiModelProperty(value = "产品名称",required = true)
	private String prodName;
	
	/**
	 * 产品编码
	 */
	@ApiModelProperty(value = "产品编码",required = true)
	private String prodCode;
	
	/**
	 * 产品类型 1-个体开户 2-个独开户 3-有限合伙开户 4-有限责任开户  5-个体开票 6-个独开票 7-有限合伙开票 8-有限责任开票    9-税务顾问 10-城市服务商
	 */
	@ApiModelProperty(value = "产品类型 1-个体开户 2-个独开户 3-有限合伙开户 4-有限责任开户  5-个体开票 6-个独开票 7-有限合伙开票 8-有限责任开票    9-税务顾问 10-城市服务商",required = true)
	private Integer prodType;

	/**
	 * 企业类型 1-个体工商户 2-个人独资企业 3-有限合伙 4-有限责任
	 */
	private Integer companyType;
	
	/**
	 * 金额
	 */
	@ApiModelProperty(value = "产品金额（原价）",required = true)
	private Long prodAmount;

	/**
	 * 会员价
	 */
	@ApiModelProperty(value = "会员价",required = true)
	private Long vipAmount;
	
	/**
	 * 金额名称
	 */
	private String amountName;
	
	/**
	 * 费用方式 1-固定金额 2-比率
	 */
	@ApiModelProperty(value = "费用方式 1-固定金额 2-比率",required = true)
	private Integer amountWay;
	
	/**
	 * 机构编码
	 */
	@ApiModelProperty(value = "机构编码",required = true)
	private String oemCode;
	
	/**
	 * 产品描述
	 */
	@ApiModelProperty(value = "产品描述",required = true)
	private String prodDesc;
	
	/**
	 * 状态 0-待上架 1-已上架 2-已下架 3-已暂停
	 */
	private Integer status;
	
	/**
	 * 备注
	 */
	private String remark;

	@ApiModelProperty(value = "园区列表",required = true)
	private List<ParkVO> parkList;// 园区列表

	/**
	 * 托管费续费金额
	 */
	private Long renewalAmount;
}
