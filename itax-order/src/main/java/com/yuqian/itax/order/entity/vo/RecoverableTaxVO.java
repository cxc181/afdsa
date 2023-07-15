package com.yuqian.itax.order.entity.vo;

import com.yuqian.itax.order.entity.OrderEntity;
import io.swagger.annotations.ApiModelProperty;
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
 * 订单表
 * 
 * @Date: 2019年12月06日 11:34:46 
 * @author 蒋匿
 */
@Getter
@Setter
public class RecoverableTaxVO implements Serializable {
	
	private static final long serialVersionUID = -1L;
	
	/**
	 * 订单号
	 */
	@ApiModelProperty(value = "订单号")
	private String orderNo;

	/**
	 *订单类型 1-充值 2-代理充值 3-提现 4-代理提现 5 - 工商注册 6-开票 7-用户升级 8-工商注销 9-证件申请 10-对公户申请 11-对公户提现 12-消费开票 13-补税 14-退税
	 */
	private Integer orderType;
	
	/**
	 * 订单状态   订单类型： 工商注册   0-待电子签字 1-待视频认证 2-审核中  3-待付款 4-待领证 5-已完成 6-已取消
	 *          开票： 0-待创建 1-待付款 2-待审核 3-出票中 4-待发货 5-出库中 6-待收货 7-已签收  8-已取消
	 *          会员升级： 0-待支付 1-支付中 2-财务审核 3-已完成 4-已取消
	 *         充值提现：0-待提交 1-支付、提现中，2-支付、提现完成，3-支付、提现失败 4-订单过期
	 */
	@ApiModelProperty(value = "工商注册：0->待电子签字；1->待视频认证；2->审核中；3->待付款；4->待领证；5->已完成；6->已取消；" +
			"开票：0->待创建；1->待付款；2->待审核；3->出票中；4->待发货；5->出库中；6->待收货；7->已签收；8->已取消" +
			"会员升级： 0->待支付 1->支付中 2->财务审核 3->已完成 4->已取消" +
			"充值提现：0->待提交 1->支付、提现中，2->支付、提现完成，3->支付、提现失败 4->订单过期")
	private Integer orderStatus;

	/**
	 * 产品名称
	 */
	@ApiModelProperty(value = "产品名称")
	private String productName;

	/**
	 * 支付金额
	 */
	@ApiModelProperty(value = "支付金额")
	private Long payAmount;


	public RecoverableTaxVO() {

	}
	public RecoverableTaxVO(OrderEntity entity) {
		this.orderNo = entity.getOrderNo();
		this.orderType = entity.getOrderType();
		this.orderStatus = entity.getOrderStatus();
		this.productName = entity.getProductName();
		this.payAmount = entity.getPayAmount();
	}
}
