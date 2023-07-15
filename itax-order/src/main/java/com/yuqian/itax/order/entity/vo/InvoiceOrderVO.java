package com.yuqian.itax.order.entity.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

/**
 * 开票订单
 * 
 * @Date: 2019年12月07日 20:05:12 
 * @author yejian
 */
@Getter
@Setter
public class InvoiceOrderVO implements Serializable {

	private static final long serialVersionUID = -1L;

	/**
	 * 订单号
	 */
	@ApiModelProperty(value = "订单号")
	private String orderNo;

	/**
	 * 集团开票订单号
	 */
	@ApiModelProperty(value = "集团开票订单号")
	private String groupOrderNo;

	/**
	 * 订单状态 0-待创建 1-待付款 2-待审核 3-出票中 4-待发货 5-出库中 6-待收货 7-已签收  8-已取消
	 */
	@ApiModelProperty(value = "订单状态：0->待创建；1->待付款；2-待审核；3-出票中；4-待发货；5-出库中；6-待收货；7-已签收；8-已取消")
	private Integer orderStatus;

	/**
	 * 开票公司id
	 */
	@ApiModelProperty(value = "开票公司id")
	private Long companyId;

	/**
	 * 开票公司名称
	 */
	@ApiModelProperty(value = "开票公司名称")
	private String companyName;

	/**
	 * 发票抬头公司名称
	 */
	@ApiModelProperty(value = "发票抬头公司名称")
	private String invHeadCompanyName;

	/**
	 * 开票金额
	 */
	@ApiModelProperty(value = "开票金额")
	private Long invoiceAmount;

	/**
	 * 添加时间
	 */
	@ApiModelProperty(value = "添加时间")
	private Date addTime;

	/**
	 * 修改时间
	 */
	@ApiModelProperty(value = "修改时间")
	private Date updateTime;

	/**
	 * 发票类型 1-普通发票 2-增值税发票
	 */
	@ApiModelProperty(value = "发票类型：1->普通发票；2-增值税发票")
	private String invoiceType;

	/**
	 * 专属客服电话
	 */
	@ApiModelProperty(value = "专属客服电话")
	private String customerServicePhone;

	/**
	 * 支付金额
	 */
	@ApiModelProperty(value = "支付金额")
	private Long payAmount;

	/**
	 * 产品名称
	 */
	@ApiModelProperty(value = "产品名称")
	private String productName;

	/**
	 * 发票抬头收件人
	 */
	@ApiModelProperty(value = "发票抬头收件人")
	private String recipient;

	/**
	 * 发票抬头联系电话
	 */
	@ApiModelProperty(value = "发票抬头联系电话")
	private String recipientPhone;

	/**
	 * 发票抬头详细地址
	 */
	@ApiModelProperty(value = "发票抬头详细地址")
	private String recipientAddress;

	/**
	 * 开票方式 1-自助开票 2-集团代开 3-佣金开票
	 */
	@ApiModelProperty(value = "开票方式 1-自助开票 2-集团代开 3-佣金开票")
	private Integer createWay;

	/**
	 * 发票类型 1-纸质发票 2-电子发票
	 */
	private Integer invoiceWay;

	/**
	 * 收票邮箱
	 */
	private String email;

	/**
	 * 发票标识 0-正常 1-已作废/红冲 2-作废重开
	 */
	private Integer invoiceMark;

	/**
	 * 企业类型 1-个体工商户 2-个人独资企业 3-有限合伙 4-有限责任
	 */
	private Integer companyType;

	/**
	 * 是否显示重新提交按钮 0-否 1-是
	 */
	private int isShowResubmit;
}
