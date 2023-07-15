package com.yuqian.itax.order.entity;

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
@Table(name="t_e_order")
public class OrderEntity implements Serializable {
	
	private static final long serialVersionUID = -1L;
	
	/**
	 * 主键id
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@ApiModelProperty(value = "主键id")
	private Long id;
	
	/**
	 * 订单号
	 */
	@ApiModelProperty(value = "订单号")
	private String orderNo;

	/**
	 * 外部订单号
	 */
	@ApiModelProperty(value = "外部订单号")
	private String externalOrderNo;

	/**
	 * 会员关系id
	 */
	@ApiModelProperty(value = "会员关系id")
	private Long relaId;

	/**
	 * 用户id
	 */
	@ApiModelProperty(value = "用户id")
	private Long userId;

	/**
	 * 用户类型 1->会员 2->城市合伙人 3->合伙人 4->平台 5->管理员 6->其他
	 */
	@ApiModelProperty(value = "用户类型")
	private Integer userType;

	/**
	 *订单类型 1-充值 2-代理充值 3-提现 4-代理提现 5 - 工商注册 6-开票 7-用户升级 8-工商注销 9-证件申请 10-对公户申请 11-对公户提现 12-消费开票 13-补税 14-退税
	 */
	private Integer orderType;
	
	/**
	 * 订单状态：工商注册(0-待电子签字,1-待视 频认证,2-审核中,3-待付款,4-待领证,5-已完成,6-已取消,7-审核失败,8-核名驳回,9-待身份验证，10-待设立登记、11-待提交签名 12-签名待确认 13-待创建),
	 * 		开票(0-待创建,1-待付款,2-待审核,3-出票中,4-待发货,5-出库中,6-待收货,7-已签收,8-已取消,9-待出款,10-审核未通过 11-待财务审核),
	 * 		会员升级( 0-待支付,1-支付中,2-财务审核,3-已完成,4-已取消),
	 * 		充值提现、对公户提现、补税，退税(0-待提交,1-支付、提现中,2-支付、提现完成,3-支付、提现失败,4-订单过期,5-已取消,6-待财务审核,7-财务审核失败),
	 * 		工商注销(0-待付款,1-注销处理中,2-税务注销成功,3-已取消 4-税单待处理 5-工商注销成功),
	 * 		证件申请( 0-待付款 1-待发货,2-出库中,3-待签收,4-已签收,5-已取消),
	 * 		公户申请（0-待付款,1-等待预约,2-已完成,3-已取消）,
	 * 		消费开票（0-待出票 1-出票中 2-已完成 3-出票失败 4-待发货 5-待签收）,
	 * 		托管费续费、对公户续费（0-待支付、1-支付中、2-已完成、3-已取消）
	 */
	@ApiModelProperty(value = "工商注册：0->待电子签字；1->待视频认证；2->审核中；3->待付款；4->待领证；5->已完成；6->已取消；" +
			"开票：0->待创建；1->待付款；2->待审核；3->出票中；4->待发货；5->出库中；6->待收货；7->已签收；8->已取消" +
			"会员升级： 0->待支付 1->支付中 2->财务审核 3->已完成 4->已取消" +
			"充值提现：0->待提交 1->支付、提现中，2->支付、提现完成，3->支付、提现失败 4->订单过期")
	private Integer orderStatus;
	
	/**
	 * 产品id
	 */
	@ApiModelProperty(value = "产品id")
	private Long productId;
	
	/**
	 * 产品名称
	 */
	@ApiModelProperty(value = "产品名称")
	private String productName;
	
	/**
	 * 机构编码
	 */
	@ApiModelProperty(value = "机构编码")
	private String oemCode;
	
	/**
	 * 园区id
	 */
	@ApiModelProperty(value = "园区id")
	private Long parkId;
	
	/**
	 * 订单金额
	 */
	@ApiModelProperty(value = "订单金额")
	private Long orderAmount;
	
	/**
	 * 优惠金额
	 */
	@ApiModelProperty(value = "优惠金额")
	private Long discountAmount;
	
	/**
	 * 支付金额
	 */
	@ApiModelProperty(value = "支付金额")
	private Long payAmount;
	
	/**
	 * 待分润金额
	 */
	@ApiModelProperty(value = "待分润金额")
	private Long profitAmount;
	
	/**
	 * 是否分润  0-待分润 1-已分润 2-分润失败
	 */
	@ApiModelProperty(value = "发票类型：0->待分润；1->已分润；2-分润失败")
	private Integer isShareProfit;
	
	/**
	 * 分润状态  0-待分润 1-分润中 2-已分润 3-分润失败
	 */
	@ApiModelProperty(value = "发票类型：0->待分润；1->分润中；2-已分润；3-分润失败")
	private Integer profitStatus;
	
	/**
	 * 添加时间
	 */
	@ApiModelProperty(value = "添加时间")
	private Date addTime;
	
	/**
	 * 添加人
	 */
	@ApiModelProperty(value = "添加人")
	private String addUser;
	
	/**
	 * 修改时间
	 */
	@ApiModelProperty(value = "修改时间")
	private Date updateTime;
	
	/**
	 * 修改人
	 */
	@ApiModelProperty(value = "修改人")
	private String updateUser;
	
	/**
	 * 备注
	 */
	@ApiModelProperty(value = "备注")
	private String remark;

	/**
	 * 支付流水图
	 */
	@ApiModelProperty(value = "支付流水图")
	private String payWaterImgs;

	/**
	 * 钱包类型 1-消费钱包 2-佣金钱包
	 */
	@ApiModelProperty(value = "钱包类型 1-消费钱包 2-佣金钱包")
	private Integer walletType;

	/**
	 * 审核状态
	 */
	@ApiModelProperty(value = "审核状态 0->待审核 1->审核通过 2->审核失败")
	private Integer auditStatus;

	/**
	 * 审核人id
	 */
	@ApiModelProperty(value = "审核人id  对应系统用户id")
	private Integer auditUserId;

	/**
	 * 审核人账号
	 */
	@ApiModelProperty(value = "审核人账号")
	private String auditUserAccount;

	/**
	 * 审核描述
	 */
	@ApiModelProperty(value = "审核描述")
	private String auditRemark;

	/**
	 * 手续费
	 */
	@ApiModelProperty(value = "手续费")
	private Long serviceFee;

	/**
	 * 手续费率
	 */
	@ApiModelProperty(value = "手续费率")
	private BigDecimal serviceFeeRate;

	/**
	 * 佣金开票订单号
	 */
	@ApiModelProperty(value = "佣金开票订单号")
	private String commissionInvoiceOrderNo;

	@ApiModelProperty(value = "操作小程序来源 1-微信小程序 2-支付宝小程序")
	private Integer sourceType;

	/**
	 * 服务商id
	 */
	private Long channelServiceId;

	/**
	 * 员工id
	 */
	private Long channelEmployeesId;

	/**
	 * 渠道编码
	 */
	private String channelCode;

	/**
	 * 推送状态：0-待推送 1-推送中 2-已推送 3-推送失败 4-无需推送
	 */
	private Integer channelPushState;

	/**
	 * 渠道产品编码
	 */
	private String channelProductCode;

	/**
	 * 渠道用户id
	 */
	private Long channelUserId;
	/**
	 * 人群标签id
	 */
	private Long crowdLabelId;

	/**
	 * 产品特价活动id
	 */
	private Long discountActivityId;

	/**
	 * 费用承担方
	 */
	private String payerName;

	/**
	 * 是否自费 1-自费 2-承担方
	 */
	private Integer isSelfPaying;

	/**
	 * 是否提交佣金提现业绩凭证 0-无需提交 1-未提交 2-已提交
	 */
	private Integer isSubmitPerformanceVoucher;
}
