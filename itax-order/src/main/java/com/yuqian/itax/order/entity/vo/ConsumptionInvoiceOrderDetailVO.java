package com.yuqian.itax.order.entity.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 消费开票订单返回详情实体
 *
 * @author yejian
 * @Date: 2020年09月28日 14:30
 */
@Getter
@Setter
public class ConsumptionInvoiceOrderDetailVO implements Serializable {

	private static final long serialVersionUID = -1L;

	/**
	 * 订单号
	 */
	@ApiModelProperty(value = "订单号")
	private String orderNo;

	/**
	 * 订单状态： 0-待出票 1-出票中 2-已出票 3-出票失败
	 */
	@ApiModelProperty(value = "订单状态： 0-待出票 1-出票中 2-已出票 3-出票失败")
	private Integer orderStatus;

	/**
	 * 开票金额
	 */
	@ApiModelProperty(value = "开票金额")
	private Long invoiceAmount;

	/**
	 * 发票方式 1-纸质发票 2-电子发票
	 */
	@ApiModelProperty(value = "发票方式 1-纸质发票 2-电子发票")
	private Integer invoiceWay;

	/**
	 * 抬头公司名称
	 */
	@ApiModelProperty(value = "抬头公司名称")
	private String companyName;

	/**
	 * 抬头公司税号
	 */
	@ApiModelProperty(value = "抬头公司税号")
	private String ein;

	/**
	 * 抬头公司地址
	 */
	@ApiModelProperty(value = "抬头公司地址")
	private String companyAddress;

	/**
	 * 抬头公司电话号码
	 */
	@ApiModelProperty(value = "抬头公司电话号码")
	private String phone;

	/**
	 * 抬头公司开户银行
	 */
	@ApiModelProperty(value = "抬头公司开户银行")
	private String bankName;

	/**
	 * 抬头公司银行账号
	 */
	@ApiModelProperty(value = "抬头公司银行账号")
	private String bankNumber;

	/**
	 * 关联消费订单号,多个订单之间用逗号分割
	 */
	@ApiModelProperty(value = "关联消费订单号,多个订单之间用逗号分割")
	private String consumptionOrderRela;

	/**
	 * 关联消费订单数量
	 */
	@ApiModelProperty(value = "关联消费订单数量")
	public Integer ralaOrderCount;

	/**
	 * 收票邮箱
	 */
	@ApiModelProperty(value = "收票邮箱")
	private String billToEmail;

	/**
	 * 电子发票地址
	 */
	@ApiModelProperty(value = "电子发票地址")
	private String invoicePdfUrl;

	/**
	 * 发票图片
	 */
	@ApiModelProperty(value = "发票图片")
	private String invoiceImgs;

	/**
	 * 发票图片电子
	 */
	private String[] invoiceImgList;

	/**
	 * 失败原因
	 */
	@ApiModelProperty(value = "失败原因")
	private String remark;

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
	 * 抬头省名称
	 */
	private String provinceName;

	/**
	 * 抬头市名称
	 */
	private String cityName;

	/**
	 * 抬头区名称
	 */
	private String districtName;
}
